package net.turanar.stellaris.visitor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.turanar.stellaris.antlr.StellarisParser.MapContext;
import net.turanar.stellaris.antlr.StellarisParser.PairContext;
import net.turanar.stellaris.domain.Modifier;
import net.turanar.stellaris.domain.ModifierType;
import net.turanar.stellaris.domain.WeightModifier;

import java.util.List;

import static net.turanar.stellaris.Global.GLOBAL_TRIGGERS;
import static net.turanar.stellaris.Global.gs;

/**
 * Builds machine-readable condition trees (weight_rules / potential_rules) that run
 * index-aligned with the pre-rendered display strings on each Technology.
 */
public class RuleBuilder {

    private static final int MAX_DEPTH = 10;

    public static JsonArray buildWeightRules(List<WeightModifier> modifiers) {
        JsonArray rules = new JsonArray();
        for(WeightModifier m : modifiers) {
            JsonObject rule = new JsonObject();
            rule.add("factor", m.factor == null ? JsonNull.INSTANCE : new JsonPrimitive(m.factor));
            if(m.add != null) rule.addProperty("add", m.add);
            JsonElement cond = m.pair == null ? null : buildCond(m.pair, 0);
            rule.add("if", cond == null ? JsonNull.INSTANCE : cond);
            rules.add(rule);
        }
        return rules;
    }

    public static JsonArray buildPotentialRules(List<Modifier> potential) {
        JsonArray rules = new JsonArray();
        for(Modifier m : potential) {
            JsonElement cond = m.pair == null ? null : buildCond(m.pair, 0);
            rules.add(cond == null ? JsonNull.INSTANCE : cond);
        }
        return rules;
    }

    private static boolean skip(String key) {
        return key.equals("exists") || key.equals("optimize_memory") || key.equals("inline_script");
    }

    private static JsonElement buildCond(PairContext pair, int depth) {
        String key = pair.key();

        switch(key) {
            case "OR":   return block("any", pair, depth);
            case "AND":  return block("all", pair, depth);
            case "NOR":  return block("none", pair, depth);
            case "NOT":  return block("none", pair, depth);
            case "NAND": {
                JsonObject inner = new JsonObject();
                inner.add("all", children(pair, depth));
                JsonArray arr = new JsonArray();
                arr.add(inner);
                JsonObject none = new JsonObject();
                none.add("none", arr);
                return none;
            }
            case "has_ethic":          return fact("has_ethic", gs(pair));
            case "has_authority":      return fact("has_authority", gs(pair));
            case "has_civic":
            case "has_valid_civic":
            case "has_government_civic": return fact("has_civic", gs(pair));
            case "has_origin":         return fact("has_origin", gs(pair));
            case "has_tradition":      return fact("has_tradition", gs(pair));
            case "has_ascension_perk": return fact("has_ascension_perk", gs(pair));
            case "has_technology":     return fact("has_technology", gs(pair));
            case "host_has_dlc":       return fact("host_has_dlc", gs(pair));
            case "has_trait_in_council": {
                String trait = param(pair, "TRAIT");
                return trait == null ? unknown(pair) : fact("has_trait_in_council", trait);
            }
            case "research_leader": {
                String trait = param(pair, "has_trait");
                return trait == null ? unknown(pair) : fact("has_trait_in_council", trait);
            }
            case "is_gestalt":
            case "is_machine_empire":
            case "is_hive_empire":
            case "is_megacorp":
            case "is_regular_empire":
            case "is_mechanical_empire": {
                JsonObject f = new JsonObject();
                f.addProperty("fact", key);
                return "no".equals(gs(pair)) ? none(f) : f;
            }
            case "always": {
                if("yes".equals(gs(pair))) return null; // always true -> "if": null
                return fact("always_false", null);
            }
        }

        if(GLOBAL_TRIGGERS != null && GLOBAL_TRIGGERS.containsKey(key)) {
            return trigger(pair, depth);
        }

        return unknown(pair);
    }

    private static JsonElement trigger(PairContext pair, int depth) {
        if(depth >= MAX_DEPTH) return unknown(pair);
        String raw = gs(pair);
        // parametrized trigger usage (name = { ARG = .. }) is not part of the simple fact table
        if(raw == null) return unknown(pair);

        PairContext body = GLOBAL_TRIGGERS.get(pair.key());
        if(body.value() == null || body.value().map() == null) return unknown(pair);

        JsonObject all = new JsonObject();
        all.add("all", children(body, depth + 1));
        return "no".equals(raw) ? none(all) : all;
    }

    private static JsonElement block(String op, PairContext pair, int depth) {
        JsonObject o = new JsonObject();
        o.add(op, children(pair, depth));
        return o;
    }

    private static JsonArray children(PairContext pair, int depth) {
        JsonArray arr = new JsonArray();
        if(pair.value() == null || pair.value().map() == null) return arr;
        MapContext map = pair.value().map();
        for(PairContext p : map.pair()) {
            if(skip(p.key())) continue;
            JsonElement c = buildCond(p, depth);
            if(c != null) arr.add(c);
        }
        return arr;
    }

    private static String param(PairContext pair, String name) {
        if(pair.value() == null || pair.value().map() == null) return null;
        for(PairContext p : pair.value().map().pair()) {
            if(p.key().equals(name)) return gs(p);
        }
        return null;
    }

    private static JsonObject fact(String name, String value) {
        JsonObject o = new JsonObject();
        o.addProperty("fact", name);
        if(value != null) o.addProperty("value", value);
        return o;
    }

    private static JsonObject none(JsonElement child) {
        JsonArray arr = new JsonArray();
        arr.add(child);
        JsonObject o = new JsonObject();
        o.add("none", arr);
        return o;
    }

    private static JsonObject unknown(PairContext pair) {
        String text;
        try {
            text = ModifierType.value(pair.key()).parse(pair);
        } catch (RuntimeException e) {
            text = pair.getText();
        }
        if(text == null || text.isEmpty()) text = pair.getText();
        JsonObject o = new JsonObject();
        o.addProperty("unknown", text);
        return o;
    }
}
