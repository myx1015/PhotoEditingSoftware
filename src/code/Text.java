package code;

import java.util.ArrayList;

public class Text{
    TextComponent t;
    ArrayList<String> text;

    public Text(TextComponent t, ArrayList<String> text) {
        this.t = t;
        this.text = text;
    }

    public TextComponent getT() {
        return t;
    }

    public void setT(TextComponent t) {
        this.t = t;
    }

}
