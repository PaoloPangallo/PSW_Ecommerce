export interface NewsletterSubscriber {
  email: string;
  confirmed?: boolean;
  confirmationToken?: string;
  subscriptionDate?: string;
}
