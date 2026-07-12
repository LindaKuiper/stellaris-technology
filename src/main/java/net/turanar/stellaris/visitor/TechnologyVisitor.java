package net.turanar.stellaris.visitor;

import net.turanar.stellaris.domain.Area;
import net.turanar.stellaris.domain.Category;
import net.turanar.stellaris.domain.Technology;
import net.turanar.stellaris.antlr.StellarisParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static net.turanar.stellaris.Global.*;

@Component
public class TechnologyVisitor {
    @Autowired
    ModifierVisitor modVisitor;

    private void visitFeatureUnlocks(Technology retval, StellarisParser.ValueContext val) {
        if(val.map() == null) return;
        for(StellarisParser.PairContext mod : val.map().pair()) {
            if(mod.key().equals("BIOLOGICAL_species_trait_points_add")) continue;
            if(mod.key().equals("show_only_custom_tooltip")) continue;
            if(mod.key().equals("description") || mod.key().equals("custom_tooltip")) {
                String key = mod.value().BAREWORD().getText();
                String effect = i18n(key);
                if(key.equals(effect)) effect = i18n("mod_" + key.toLowerCase());
                effect = effect.replace("$POINTS|0=+$","+1");
                retval.feature_unlocks.add(effect);
            } else if (!mod.key().startsWith("description")){
                String key = mod.key().toLowerCase();

                if(key.equals("science_ship_survey_speed")) key = "mod_ship_science_survey_speed";
                if(key.equals("ship_anomaly_generation_chance_mult")) key = "mod_ship_anomaly_generation_chance";
                if(key.equals("ship_anomaly_research_speed_mult")) key = "mod_ship_anomaly_research_speed";
                if(key.equals("all_technology_research_speed")) key = "all_tech_research_speed";
                if(key.equals("army_health")) key = "mod_army_health";

                String effect = i18n(key);
                if(key.equals("species_leader_exp_gain")) effect = "Species Leader Exp Gain";
                if(key.equals(effect)) effect = i18n("mod_" + key);
                if(effect.startsWith("mod_")) effect = i18n("mod_country_" + key);

                String value = "";
                if(mod.value() == null) {
                    value = mod.getText();
                } else if (mod.value().NUMBER() != null) {
                    value = mod.value().NUMBER().getText();
                    if(value.contains(".")) {
                        NumberFormat nf = new DecimalFormat("+#;-#");
                        value = nf.format(Float.valueOf(value)*100.0f) + "%";
                    } else {
                        value = '+' + value;
                    }
                } else if (mod.value().BOOLEAN() != null) {
                    value = mod.value().BOOLEAN().getText();
                }

                //System.out.println("\t" + effect + " " + value);
                retval.feature_unlocks.add(effect + " " + value);
            }
        }
    }

    // Since 4.x, cost/weight may be a block like { factor = @var inline_script = { ... } }
    private String scalar(StellarisParser.PairContext pair) {
        if(pair.value().map() != null) {
            for(StellarisParser.PairContext inner : pair.value().map().pair()) {
                if(inner.key().equals("factor") || inner.key().equals("base")) return gs(inner);
            }
            return null;
        }
        return gs(pair);
    }

    // Prerequisites may contain OR = { ... } blocks since 4.x - flatten them, skipping the OR keyword
    private void addPrerequisites(Technology retval, StellarisParser.ArrayContext array) {
        array.value().forEach(val -> {
            if(val.array() != null) { addPrerequisites(retval, val.array()); return; }
            String preq = gs(val);
            if(preq == null || preq.equals("OR")) return;
            retval.prerequisites.add(preq.replaceAll("\"",""));
        });
    }

    public Technology visitPair(StellarisParser.PairContext ctx) {
        Technology retval = new Technology();

        retval.key = ctx.key();
        retval.name = i18n(retval.key);
        retval.description = i18n(retval.key + "_desc");

        for(StellarisParser.PairContext pair : ctx.value().map().pair()) {
            try {
            switch (pair.key()) {
                case "cost": {
                    String c = scalar(pair);
                    if(c != null) retval.cost = Math.round(Float.valueOf(c));
                    break;
                }
                case "tier":
                    retval.tier = Integer.valueOf(gs(pair)); break;
                case "area":
                    retval.area = Area.valueOf(gs(pair)); break;
                case "category":
                    StellarisParser.ValueContext v = pair.value().array().value().get(0);
                    retval.category = Category.eval(i18n(gs(v))); break;
                case "start_tech":
                    retval.is_start_tech = gbool(pair); break;
                case "is_rare":
                    retval.is_rare = gbool(pair); break;
                case "is_dangerous":
                    retval.is_dangerous = gbool(pair); break;
                case "weight": {
                    String w = scalar(pair);
                    if(w != null) retval.base_weight = Float.valueOf(w);
                    break;
                }
                case "weight_modifier":
                    retval.weight_modifiers = modVisitor.visitPair(retval, pair); break;
                case "feature_flags":
                    pair.value().array().value().forEach(val -> {
                        retval.feature_unlocks.add("<b>Feature : </b>" + i18n("feature_" + gs(val)));
                    }); break;
                case "potential":
                    retval.potential.addAll(modVisitor.visitPotential(pair)); break;
                case "modifier":
                    visitFeatureUnlocks(retval, pair.value()); break;
                case "prerequisites":
                    if(pair.value().array() == null) break;
                    addPrerequisites(retval, pair.value().array());

            }
            } catch (RuntimeException e) {
                throw new RuntimeException("Failed to parse '" + pair.key() + "' of tech " + retval.key + " (value: " + pair.value().getText() + ")", e);
            }
        }
        return retval;
    }
}
