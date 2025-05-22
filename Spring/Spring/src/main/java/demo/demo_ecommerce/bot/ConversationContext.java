// 1. ConversationContext.java
package demo.demo_ecommerce.bot;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConversationContext {
    private String currentIntent;
    private String state;
    private int step;

    public ConversationContext() {
        this.state = "IDLE";
        this.step = 0;
    }

    public void incrementStep() {
        this.step++;
    }
}