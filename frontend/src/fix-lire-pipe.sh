#!/bin/bash

echo "🔍 Correzione uso di LirePipe..."

# Sostituisci tutte le occorrenze di | lire:true con | lire:false nei file .html
find . -type f -name "*.html" -exec sed -i 's/| *lire:true/| lire:false/g' {} +

echo "✅ Correzione completata. Controlla i file aggiornati."
