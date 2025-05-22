package demo.demo_ecommerce.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import demo.demo_ecommerce.dtos.OrderDTO;
import demo.demo_ecommerce.entities.*;
import demo.demo_ecommerce.entities.Order.ShippingMethod;
import demo.demo_ecommerce.repositories.CartRepository;
import demo.demo_ecommerce.repositories.OrderRepository;
import demo.demo_ecommerce.repositories.UsersRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;


    private final UsersRepository userRepository;
    private final CartRepository cartRepository;

    public OrderService(OrderRepository orderRepository,
                        UsersRepository userRepository,
                        CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public OrderDTO createOrder(Long userId, ShippingMethod shippingMethod) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato con ID: " + userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Carrello non trovato per l'utente con ID: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Il carrello è vuoto, impossibile creare un ordine");
        }

        // Calcolo totale carrello con eventuali sconti
        BigDecimal totalBD = cart.getItems().stream()
                .map(cartItem -> {
                    BigDecimal price = cartItem.getProduct().getPrice();
                    if (cartItem.getAppliedCoupon() != null) {
                        BigDecimal discount = cartItem.getAppliedCoupon().getDiscountPercentage();
                        BigDecimal discountRate = BigDecimal.valueOf(100)
                                .subtract(discount)
                                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                        price = price.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
                    }
                    return price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalBD.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Il totale dell'ordine deve essere maggiore di zero");
        }

        // Calcolo del costo di spedizione
        BigDecimal shippingCost = calculateShippingCost(totalBD, shippingMethod);

        // Crea ordine
        Order unsavedOrder = Order.builder()
                .user(user)
                .total(totalBD)
                .shippingMethod(shippingMethod)
                .shippingCost(shippingCost)
                .build();

        // Crea OrderItem
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    BigDecimal productPrice = cartItem.getProduct().getPrice();
                    if (cartItem.getAppliedCoupon() != null) {
                        BigDecimal discount = cartItem.getAppliedCoupon().getDiscountPercentage();
                        BigDecimal discountRate = BigDecimal.valueOf(100)
                                .subtract(discount)
                                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                        productPrice = productPrice.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
                    }

                    Product product = cartItem.getProduct();
                    int quantity = cartItem.getQuantity();
                    if (product.getStock() < quantity) {
                        throw new IllegalArgumentException("Stock insufficiente per il prodotto: " + product.getName());
                    }
                    product.setStock(product.getStock() - quantity);

                    return OrderItem.builder()
                            .order(unsavedOrder)
                            .product(product)
                            .quantity(quantity)
                            .price(productPrice)
                            .build();
                })
                .collect(Collectors.toList());

        unsavedOrder.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(unsavedOrder);

        // Svuota carrello
        cart.getItems().clear();
        cartRepository.save(cart);

        return OrderDTO.fromEntity(savedOrder, true); // o false se vuoi evitare il caricamento degli items
    }

    private BigDecimal calculateShippingCost(BigDecimal total, ShippingMethod method) {
        if (total.compareTo(new BigDecimal("100.00")) >= 0) {
            return BigDecimal.ZERO;
        }

        return switch (method) {
            case STANDARD -> new BigDecimal("4.99");
            case EXPRESS -> new BigDecimal("9.99");
            case PREMIUM -> new BigDecimal("14.99");
        };
    }

    @Transactional
    public Page<OrderDTO> getOrdersByUserId(Long userId, Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findByUserIdFetchItems(userId, pageable);
        ordersPage.getContent().forEach(order -> {
            if (!Hibernate.isInitialized(order.getOrderItems())) {
                Hibernate.initialize(order.getOrderItems());
            }
        });
        return ordersPage.map(order -> OrderDTO.fromEntity(order, true));
    }

    @Transactional
    public OrderDTO getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserIdFetchItems(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Ordine non trovato: " + orderId));
        return OrderDTO.fromEntity(order, true);

    }

    public byte[] generateInvoicePdf(Long userId, Long orderId) throws IOException, DocumentException {
        Optional<Order> optionalOrder = orderRepository.findByUserIdAndId(userId, orderId);
        if (optionalOrder.isEmpty()) {
            throw new IllegalArgumentException("Ordine non trovato");
        }

        Order order = optionalOrder.get();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, out);
        document.open();

        // Logo
        try {
            URL imageUrl = getClass().getClassLoader().getResource("static/logo_unilire.png");
            if (imageUrl != null) {
                Image logo = Image.getInstance(imageUrl);
                logo.scaleToFit(150, 80);
                logo.setAlignment(Image.ALIGN_CENTER);
                document.add(logo);
            } else {
                document.add(new Paragraph("Unilire", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            document.add(new Paragraph("Unilire", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
        }


        // Font
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);

        document.add(new Paragraph("Fattura Ordine #" + order.getId(), titleFont));
        document.add(Chunk.NEWLINE);

        User user = order.getUser();
        String indirizzo = String.format("%s, %s %s (%s) - %s",
                safe(user.getAddress()), safe(user.getCap()), safe(user.getCity()),
                safe(user.getRegion()), safe(user.getCountry())
        );

        document.add(new Paragraph("Cliente: " + safe(user.getUsername()), normalFont));
        document.add(new Paragraph("Email: " + safe(user.getEmail()), normalFont));
        document.add(new Paragraph("Indirizzo: " + indirizzo, normalFont));
        document.add(new Paragraph("Data ordine: " + order.getCreatedAt(), normalFont));
        document.add(new Paragraph("Spedizione: " + safe(order.getShippingMethod() != null ? order.getShippingMethod().name() : "-"), normalFont));
        document.add(Chunk.NEWLINE);

        // Linea divisoria
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BaseColor.LIGHT_GRAY);
        document.add(new Chunk(separator));
        document.add(Chunk.NEWLINE);

        List<OrderItem> items = order.getOrderItems();
        if (items == null || items.isEmpty()) {
            document.add(new Paragraph("⚠ Nessun prodotto presente nell’ordine.", sectionFont));
        } else {
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 1.2f, 2, 2});

            // Header
            addCell(table, "Prodotto", sectionFont, BaseColor.LIGHT_GRAY);
            addCell(table, "Quantità", sectionFont, BaseColor.LIGHT_GRAY);
            addCell(table, "Prezzo", sectionFont, BaseColor.LIGHT_GRAY);
            addCell(table, "Totale", sectionFont, BaseColor.LIGHT_GRAY);

            BigDecimal subtotal = BigDecimal.ZERO;

            for (OrderItem item : items) {
                String name = safe(item.getProduct() != null ? item.getProduct().getName() : "-");
                int quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
                BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));

                subtotal = subtotal.add(total);

                addCell(table, name, normalFont, BaseColor.WHITE);
                addCell(table, String.valueOf(quantity), normalFont, BaseColor.WHITE);
                addCell(table, price.setScale(2, RoundingMode.HALF_UP) + " €", normalFont, BaseColor.WHITE);
                addCell(table, total.setScale(2, RoundingMode.HALF_UP) + " €", normalFont, BaseColor.WHITE);
            }

            document.add(table);
            document.add(Chunk.NEWLINE);

            // Totali
            BigDecimal discount = BigDecimal.ZERO;
            if (order.getCoupon() != null && order.getCoupon().getDiscountPercentage() != null) {
                BigDecimal perc = order.getCoupon().getDiscountPercentage();
                discount = subtotal.multiply(perc).divide(BigDecimal.valueOf(100));
                document.add(new Paragraph("Sconto coupon (" + order.getCoupon().getCode() + "): -" + discount.setScale(2, RoundingMode.HALF_UP) + " €", normalFont));
            }

            BigDecimal shipping = order.getShippingCost() != null ? order.getShippingCost() : BigDecimal.ZERO;
            document.add(new Paragraph("Spese di spedizione: " + shipping.setScale(2, RoundingMode.HALF_UP) + " €", normalFont));

            BigDecimal total = subtotal.subtract(discount).add(shipping);
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("💰 Totale da pagare: " + total.setScale(2, RoundingMode.HALF_UP) + " €", totalFont));
        }

        document.close();
        return out.toByteArray();
    }

    // Aggiunge celle con background
    private void addCell(PdfPTable table, String text, Font font, BaseColor bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setPadding(6);
        table.addCell(cell);
    }


    private String safe(String val) {
        return val != null ? val : "-";
    }


    public String getOrderStatus(Long userId, Long orderId) {
        Optional<Order> opt = orderRepository.findById(orderId);

        if (opt.isEmpty()) {
            System.out.println("❌ Ordine con ID " + orderId + " non esiste affatto.");
            return "❓ Ordine non trovato.";
        }

        Order ordine = opt.get();

        // 👇 DEBUG STAMPE CRITICHE
        System.out.println("✅ Ordine trovato");
        System.out.println("👉 userId richiesto = " + userId);
        System.out.println("👉 userId ordine   = " + ordine.getUser().getId());

        if (!Objects.equals(ordine.getUser().getId(), userId)) {
            return "🚫 Questo ordine appartiene all’utente #" + ordine.getUser().getId() + ", non a te (#" + userId + ").";
        }

        // Restituisci stato reale
        return switch (ordine.getStatus()) {
            case CREATED -> "📝 Ordine appena creato";
            case PAID -> "💳 Pagato";
            case SHIPPED -> "🚚 Spedito";
            case DELIVERED -> "📦 Consegnato";
            case CANCELLED -> "❌ Annullato";
        };
    }





}



