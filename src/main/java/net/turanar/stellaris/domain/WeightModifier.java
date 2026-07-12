package net.turanar.stellaris.domain;

public class WeightModifier extends Modifier {
    @Override
    public String toString() {
        String format = "(×%s)";
        if(add != null && add > 0) format = "(+%s)";
        if(type != null) format += " %s";

        String s_factor = "";
        if (factor != null && factor>= 1.0f) s_factor = "<b style='color:lime'>" + factor + "</b>";
        if (factor != null && factor < 1.0f) s_factor = "<b style='color:red'>" + factor + "</b>";
        String condition = "";
        if(type != null) {
            try {
                condition = type.parse(pair).replaceAll("\\n","<br/>");
            } catch (RuntimeException e) {
                condition = pair != null ? pair.getText() : "";
            }
        }
        return String.format(format, s_factor, condition);
    }
}
