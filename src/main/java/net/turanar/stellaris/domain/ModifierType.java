package net.turanar.stellaris.domain;

import net.turanar.stellaris.antlr.StellarisParser.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import static net.turanar.stellaris.Global.*;

public enum ModifierType {
    has_ascension_perk("Has %s Ascension Perk"),
    has_authority("Has %s Authority"),
    has_blocker("Has £blocker£ Tile Blocker: %s"),
    has_technology("Has Technology: %s"),
    has_valid_civic("Has Government Civic: %s"),
    has_civic("Has Government Civic: %s"),
    has_modifier("Has the %s modifier"),
    has_ethic("Has %s Ethic"),
    host_has_dlc("Has DLC %s"),
    has_tradition("Has %s Tradition"),
    has_country_flag("Has the %s country flag"),
    has_global_flag("Has the %s global flag"),
    has_deposit("Has deposit %s"),
    is_country_type("Is of country type: %s"),
    is_planet_class("Is %s"),
    has_communications("Has communication with our Empire"),
    pop_has_trait("Pop has trait %s"),
    has_policy_flag((p) -> f("Has policy %s", i18n(gs(p) + "_name"))),
    owns_any_bypass((p) -> f("Controls a system with a %s", i18n("bypass_" + gs(p).toLowerCase()))),
    has_seen_any_bypass((p) -> f("Has encountered a %s", i18n("bypass_" + gs(p).toLowerCase()))),

    is_xenophile(DefaultParser.SCRIPTED),
    is_pacifist(DefaultParser.SCRIPTED),
    is_materialist(DefaultParser.SCRIPTED),
    is_egalitarian(DefaultParser.SCRIPTED),
    is_authoritarian(DefaultParser.SCRIPTED),
    is_militarist(DefaultParser.SCRIPTED),
    is_xenophobe(DefaultParser.SCRIPTED),

    is_spiritualist(DefaultParser.SCRIPTED),
    is_gestalt(DefaultParser.SCRIPTED),
    is_mechanical_empire(DefaultParser.SCRIPTED),
    is_regular_empire(DefaultParser.SCRIPTED),
    is_machine_empire(DefaultParser.SCRIPTED),
    is_hive_empire(DefaultParser.SCRIPTED),
    is_megacorp(DefaultParser.SCRIPTED),
    allows_slavery(DefaultParser.SCRIPTED),
    has_ancrel(DefaultParser.SCRIPTED),

    is_ai("Is AI|Is NOT AI", DefaultParser.SIMPLE_BOOLEAN),

    is_enslaved("Pop is enslaved|Pop is NOT enslaved", DefaultParser.SIMPLE_BOOLEAN),
    is_sapient("Pop is Sapient|Pop is NOT Sapient", DefaultParser.SIMPLE_BOOLEAN),
    has_any_megastructure_in_empire("Has any Megastructure|Does NOT have any Megastructure",DefaultParser.SIMPLE_BOOLEAN),
    always("Always|Never", DefaultParser.SIMPLE_BOOLEAN),

    years_passed("Number of years since game start is %s %s", DefaultParser.SIMPLE_OPERATION),
    num_owned_planets("Number of owned planets is %s %s", DefaultParser.SIMPLE_OPERATION),
    num_communications("Number of communications is %s %s", DefaultParser.SIMPLE_OPERATION),
    has_level("Skill level is %s %s", DefaultParser.SIMPLE_OPERATION),

    any_neighbor_country("Any Neighbor Country", DefaultParser.CONDITIONAL),
    any_owned_planet("Any Owned Planet", DefaultParser.CONDITIONAL),
    any_planet_within_border("Any Planet within borders", DefaultParser.CONDITIONAL),
    any_planet("Any Planet", DefaultParser.CONDITIONAL),
    any_owned_pop("Any Owned Pop", DefaultParser.CONDITIONAL),
    any_system_within_border("Any System within borders", DefaultParser.CONDITIONAL),
    any_system_planet("Any Planet", DefaultParser.CONDITIONAL),
    any_relation("Any Country Relation",DefaultParser.CONDITIONAL),
    any_pop("Any Pop", DefaultParser.CONDITIONAL),
    owner_species("Founder Species :", DefaultParser.CONDITIONAL),
    no_scope("", DefaultParser.CONDITIONAL),

    NOR("All must be false", DefaultParser.CONDITIONAL),
    OR("One must be true", DefaultParser.CONDITIONAL),
    NAND("One or more must be false", DefaultParser.CONDITIONAL),
    AND("All must be true", DefaultParser.CONDITIONAL),

    has_trait((p) -> {
        String expertise = traitName(gs(p));
        if(expertise.contains("Expertise: ")) expertise = expertise.replaceAll("Expertise: ","") + " Expert";
        return "Is " + expertise;
    }),
    area((p) -> StringUtils.capitalize(gs(p))),
    research_leader((p) -> {
        String area = "";
        List<String> conditions = new ArrayList<>();
        for(PairContext prop : p.value().map().pair()) {
            Modifier m = visitCondition(prop);
            if(m.type.equals(ModifierType.area)) area = m.toString();
            else conditions.add(m.toString());
        }
        String retval = "Research Leader (" + area + "): ";
        for(int i = 0; i < conditions.size(); i++) {
            retval = retval + "\n" + LS + conditions.get(i);
        }
        return retval;
    }),

    has_resource((p) -> {
        String type = "";
        String count = "";
        for(PairContext prop : p.value().map().pair()) {
            if(prop.key().equals("type")) {
                type = gs(prop);
            } else if (prop.key().equals("amount")) {
                count = op(prop) + " " + gs(prop);
            }
        }
        return "Has £" + type + "£ " + i18n(type) + " " +  count;
    }),

    count_starbase_sizes((p) -> {
        String retval = "Number of %s is %s %s";
        String size = null, operator = null, count = null;
        for(PairContext prop : p.value().map().pair()) {
            if(prop.key().equals("starbase_size")) {
                size = i18n(gs(prop));
            } else if (prop.key().equals("count")) {
                operator = op(prop);
                count = gs(prop);
            }
        }
        return String.format(retval, size, operator, count);
    }),

    num_districts((p)->{
        String type = "";
        String count = "";
        for(PairContext prop : p.value().map().pair()) {
            if(prop.key().equals("type")) {
                type = i18n(gs(prop));
            } else if (prop.key().equals("value")) {
                count = op(prop) + " " + gs(prop);
            }
        }
        return "Number of " + type + " is " + count;
    }),

    count_owned_pops((p) -> {
        String limits = "";
        String count = "";
        for(PairContext prop : p.value().map().pair()) {
            if(prop.key().equals("limit")) {
                for(PairContext l : prop.value().map().pair()) {
                    Modifier m = visitCondition(l);
                    limits += "\n" + LS + m.toString();
                }
            } else if(prop.key().equals("count")) {
                count = op(prop) + " " + gs(prop);
            }
        }
        return "Has a number of pop " + count + limits;
    }),

    NOT((p) -> {
        if(p.value().map().pair().size() > 1) return NOR.parser.apply(p);
        Modifier m = visitCondition(p.value().map().pair().get(0));
        if(m.type.equals(OR)) return NOR.parser.apply(p.value().map().pair().get(0));

        String retval = m.toString();
        if(retval.startsWith("Has")) {
            return "Does NOT " + retval.replaceFirst("Has", "have");
        }
        else if(retval.startsWith("Is")) {
            return "Is NOT " + retval.replaceFirst("Is","");
        }  else if(retval.startsWith("Any")) {
            return retval.replaceFirst("Any","No");
        } else {
            return "NOT " + retval;
        }
    }),

    // Scope-existence checks (exists = this/from/...) carry no player-facing meaning
    exists((p) -> ""),

    // 4.x conditions
    has_origin("Has %s Origin"),
    founder_species("Founder Species :", DefaultParser.CONDITIONAL),
    species("Species :", DefaultParser.CONDITIONAL),
    owner_or_space_owner("Owner", DefaultParser.CONDITIONAL),
    federation("Federation :", DefaultParser.CONDITIONAL),
    any_owned_pop_group("Any Owned Pop", DefaultParser.CONDITIONAL),
    any_owned_species("Any Owned Species", DefaultParser.CONDITIONAL),
    any_owned_nonprimary_starbase("Any Owned Starbase", DefaultParser.CONDITIONAL),
    any_country("Any Country", DefaultParser.CONDITIONAL),
    any_owned_leader("Any Owned Leader", DefaultParser.CONDITIONAL),
    is_nomadic("Is Nomadic|Is NOT Nomadic", DefaultParser.SIMPLE_BOOLEAN),
    has_relic((p) -> {
        String name = i18n(gs(p) + "_name");
        if(name.endsWith("_name")) name = i18n(gs(p));
        return "Has Relic: " + name;
    }),
    has_federation("Is in a Federation|Is NOT in a Federation", DefaultParser.SIMPLE_BOOLEAN),
    is_galactic_community_member("Is a Galactic Community member|Is NOT a Galactic Community member", DefaultParser.SIMPLE_BOOLEAN),
    mid_game_years_passed("Number of years since mid-game is %s %s", DefaultParser.SIMPLE_OPERATION),
    num_ascension_perk_slots("Number of Ascension Perk slots is %s %s", DefaultParser.SIMPLE_OPERATION),
    num_cosmic_storms_encountered("Number of cosmic storms encountered is %s %s", DefaultParser.SIMPLE_OPERATION),

    has_trait_in_council((p) -> {
        for(PairContext prop : p.value().map().pair()) {
            if(prop.key().equals("TRAIT")) return "Has council member with trait: " + traitName(gs(prop));
        }
        return "Has council member with a specific trait";
    }),

    is_specialist_subject_type((p) -> {
        for(PairContext prop : p.value().map().pair()) {
            if(prop.key().equals("TYPE")) return "Is " + i18n(gs(prop)) + " specialist subject";
        }
        return "Is a specialist subject";
    }),

    num_buildings((p) -> {
        String type = "", count = "";
        for(PairContext prop : p.value().map().pair()) {
            if(prop.key().equals("type")) type = i18n(gs(prop));
            else if(prop.key().equals("value")) count = op(prop) + " " + gs(prop);
        }
        return "Number of " + type + " buildings is " + count;
    }),

    count_archaeological_site((p) -> {
        String count = "";
        for(PairContext prop : p.value().map().pair()) {
            if(prop.key().equals("count")) count = op(prop) + " " + gs(prop);
        }
        return "Number of archaeological sites is " + count;
    }),

    count_owned_pop_amount((p) -> {
        String limits = "";
        String count = "";
        for(PairContext prop : p.value().map().pair()) {
            if(prop.key().equals("limit")) {
                for(PairContext l : prop.value().map().pair()) {
                    String s = visitCondition(l).toString();
                    if(!s.isEmpty()) limits += "\n" + LS + s;
                }
            } else if(prop.key().equals("count")) {
                count = op(prop) + " " + gs(prop);
            }
        }
        return "Has a number of pops " + count + limits;
    }),

    calc_true_if((p) -> {
        String amount = "";
        List<String> conditions = new ArrayList<>();
        for(PairContext prop : p.value().map().pair()) {
            if(prop.key().equals("amount")) amount = op(prop) + " " + gs(prop);
            else {
                String s = visitCondition(prop).toString();
                if(!s.isEmpty()) conditions.add(s);
            }
        }
        String retval = "A number " + amount + " of the following must be true";
        for(String condition : conditions) {
            retval = retval + "\n" + LS + condition;
        }
        return retval;
    }),

    IF_BLOCK((p) -> {
        List<String> conditions = new ArrayList<>();
        for(PairContext prop : p.value().map().pair()) {
            if(prop.key().equals("limit")) {
                for(PairContext l : prop.value().map().pair()) {
                    String s = visitCondition(l).toString();
                    if(!s.isEmpty()) conditions.add(s);
                }
            }
        }
        String retval = "If";
        for(String condition : conditions) {
            retval = retval + "\n" + LS + condition;
        }
        return retval;
    }),

    // Generic fallback for any condition that matches a scripted trigger by name
    scripted_trigger(DefaultParser.SCRIPTED),

    solar_system("Solar System", DefaultParser.CONDITIONAL),
    any_member("Any Federation Member", DefaultParser.CONDITIONAL),

    ELSE_BLOCK((p) -> {
        List<String> conditions = new ArrayList<>();
        for(PairContext prop : p.value().map().pair()) {
            if(prop.key().equals("limit")) {
                for(PairContext l : prop.value().map().pair()) {
                    String s = visitCondition(l).toString();
                    if(!s.isEmpty()) conditions.add(s);
                }
            }
        }
        String retval = "Otherwise";
        for(String condition : conditions) {
            retval = retval + "\n" + LS + condition;
        }
        return retval;
    }),

    DEFAULT((p) -> {
        // Unknown condition - render a readable name with its arguments instead of raw script
        StringBuilder sb = new StringBuilder(StringUtils.capitalize(p.key().replace('_', ' ')));
        if(p.value() != null && p.value().map() != null) {
            for(PairContext arg : p.value().map().pair()) {
                String v = gs(arg);
                if(v != null) sb.append(": ").append(i18n(v));
            }
        } else {
            String v = gs(p);
            if(v != null && !v.equals("yes")) sb.append(": ").append(i18n(v));
        }
        return sb.toString();
    })
    ;

    private static enum DefaultParser {
        SIMPLE((format,p) -> String.format(format,i18n(gs(p.value())))),
        SIMPLE_OPERATION((format,p) -> String.format(format, op(p), gs(p))),
        SIMPLE_BOOLEAN((format,p) -> {
            String[] sentence = format.split("\\|");
            if(gs(p).equals("yes")) return sentence[0]; else return sentence[1];
        }),
        CONDITIONAL((format, p) -> {
            List<String> conditions = new ArrayList<>();

            for(PairContext prop : p.value().map().pair()) {
                Modifier m = visitCondition(prop);
                String s = m.toString();
                if(!s.isEmpty()) conditions.add(s);
            }

            String retval = format;
            for(int i = 0; i < conditions.size(); i++) {
                retval = retval + "\n" + LS + conditions.get(i).replaceAll(LS, "\t" + LS);
            }
            return retval;
        }),
        SCRIPTED((format, p) -> {
            PairContext q = GLOBAL_TRIGGERS.get(p.key());
            String raw = gs(p);
            if(raw == null) {
                // Parametrized trigger usage (name = { ARG = ... }) - render a readable name with its arguments
                StringBuilder sb = new StringBuilder(StringUtils.capitalize(p.key().replace('_', ' ')));
                if(p.value().map() != null) {
                    for(PairContext arg : p.value().map().pair()) {
                        String v = gs(arg);
                        sb.append(": ").append(i18n(v == null ? arg.getText() : v));
                    }
                }
                return sb.toString();
            }
            boolean value = raw.equals("yes");
            List<String> conditions = new ArrayList<>();

            if(!value) {
                return ModifierType.NOT.parse(q);
            }

            for(PairContext prop : q.value().map().pair()) {
                Modifier m = visitCondition(prop);
                String s = m.toString();
                if(!s.isEmpty()) conditions.add(s);
            }
            String retval = format;

            if(conditions.isEmpty()) return "";
            if(conditions.size() < 2) {
                retval = conditions.get(0);
                return retval;
            }

            for(int i = 0; i < conditions.size(); i++) {
                retval = retval + "\n" + LS + conditions.get(i).replaceAll(LS, "\t" + LS);
            }
            return retval;
        });

        private BiFunction<String, PairContext, String> parser;

        private DefaultParser(BiFunction<String, PairContext,String> parser) {
            this.parser = parser;
        }

        public String apply(String format, PairContext pair) {
            return this.parser.apply(format, pair);
        }
    }

    private Function<PairContext,String> parser;

    ModifierType(String format, DefaultParser parser) {
        this.parser = (p) -> parser.apply(format, p);
    }

    ModifierType(DefaultParser parser) {
        this.parser = (p) -> parser.apply(null, p);
    }

    ModifierType(Function<PairContext,String> parser) {
        this.parser = parser;
    }

    ModifierType(String format) {
        this(format, DefaultParser.SIMPLE);
    }

    public String parse(PairContext pair) {
        return parser.apply(pair);
    }

    public static ModifierType value(String name) {
        if(name.equals("if")) return IF_BLOCK; // "if"/"else" cannot be enum constants
        if(name.equals("else")) return ELSE_BLOCK;
        try {
            return valueOf(name);
        } catch (IllegalArgumentException e) {
            if(GLOBAL_TRIGGERS != null && GLOBAL_TRIGGERS.containsKey(name)) return scripted_trigger;
            return DEFAULT;
        }
    }

    public static Modifier visitCondition(PairContext pair) {
        Modifier retval = new Modifier();
        retval.type = ModifierType.value(pair.key());
        retval.pair = pair;
        return retval;
    }
}
