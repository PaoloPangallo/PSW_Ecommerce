// 1. ConversationContext.java
package demo.demo_ecommerce.bot;

public class ConversationContext {
    private String currentIntent;
    private String state;
    private int step;

    public ConversationContext() {
        this.state = "IDLE";
        this.step = 0;
    }

    public String getCurrentIntent() {
        return currentIntent;
    }

    public void setCurrentIntent(String currentIntent) {
        this.currentIntent = currentIntent;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public void incrementStep() {
        this.step++;
    }
}