package net.turanar.stellaris.visitor;

import net.turanar.stellaris.domain.Modifier;
import net.turanar.stellaris.domain.ModifierType;
import net.turanar.stellaris.domain.Technology;
import net.turanar.stellaris.domain.WeightModifier;
import net.turanar.stellaris.antlr.StellarisParser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static net.turanar.stellaris.Global.gs;

@Component
public class ModifierVisitor {

    public ArrayList<Modifier> visitPotential(StellarisParser.PairContext ctx) {
        ArrayList<Modifier> retval = new ArrayList<>();

        ctx.value().map().pair().forEach(p -> {
            if(p.key().equals("inline_script")) return;
            Modifier m = new Modifier();
            m.type = ModifierType.value(p.key());
            m.pair = p;
            retval.add(m);
        });
        return retval;
    }

    // Factors may be script values like "value:tech_weight_likelihood" which cannot be evaluated here
    private Float parseFactor(String value) {
        if(value == null) return null;
        try {
            return Float.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public List<WeightModifier> visitPair(Technology tech, StellarisParser.PairContext ctx) {
        List<WeightModifier> retval = new ArrayList<WeightModifier>();
        ctx.value().map().pair().forEach(p -> {
            switch(p.key()) {
                case "factor": {
                    Float f = parseFactor(gs(p));
                    if(f != null) tech.base_factor = f;
                    break;
                }
                case "modifier":
                    WeightModifier m = visitModifier(p);
                    if(m.pair == null && m.factor != null) tech.base_factor = m.factor;
                    else retval.add(m);
                    break;
            }
        });
        return retval;
    }

    public WeightModifier visitModifier(StellarisParser.PairContext ctx) {
        WeightModifier retval = new WeightModifier();
        ctx.value().map().pair().forEach(p -> {
            switch(p.key()) {
                case "inline_script": break;
                case "factor": retval.factor = parseFactor(gs(p)); break;
                case "add": {
                    Float a = parseFactor(gs(p));
                    if(a != null) retval.add = Math.round(a);
                    break;
                }
                default:
                    retval.type = ModifierType.value(p.key());
                    retval.pair = p;
            }
        });
        return retval;
    }
}
