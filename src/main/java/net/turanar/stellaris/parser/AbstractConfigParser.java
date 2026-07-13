package net.turanar.stellaris.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.turanar.stellaris.domain.Modifier;
import net.turanar.stellaris.domain.ModifierType;
import net.turanar.stellaris.domain.Technology;
import net.turanar.stellaris.visitor.RuleBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static net.turanar.stellaris.Global.gs;
import static net.turanar.stellaris.Global.i18n;
import static net.turanar.stellaris.Global.traitName;

public abstract class AbstractConfigParser {
    @Autowired
    protected Map<String, Technology> technologies;
    @Autowired
    Gson gson;

    public void writeJson(String filename, Technology... techs) throws IOException {
        FileOutputStream fos = new FileOutputStream("output/" + filename);
        String data;
        if(techs.length == 1) { data = gson.toJson(techs[0]); }
        else {
            Technology root = new Technology();
            Arrays.stream(techs).forEach(t -> root.children.addAll(t.children));
            data = gson.toJson(root);
        }
        fos.write(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        fos.close();
    }

    /**
     * Attach child under parent, inserting invisible Treant pseudo nodes when the
     * child's tier is more than one level deeper - so every tier gets its own column.
     */
    public static void attach(Technology parent, Technology child, int parentTier) {
        int childTier = child.tier == null ? 0 : child.tier;
        Technology anchor = parent;
        for(int i = parentTier + 1; i < childTier; i++) {
            Technology filler = new Technology();
            filler.pseudo = true;
            filler.key = "pseudo_" + child.key + "_tier" + i;
            filler.area = child.area;
            filler.category = child.category;
            filler.cost = child.cost;
            filler.tier = i;
            anchor.children.add(filler);
            anchor = filler;
        }
        anchor.children.add(child);
    }

    public void prepare(Map<String,Technology> technologies) {
        technologies.values().forEach(tech -> {
            if(tech.tier == null) tech.tier = 0;
            if(tech.cost == null) tech.cost = 0;
            if(tech.is_start_tech) tech.prerequisites.clear();

            Iterator<String> iter = tech.prerequisites.iterator();
            while(iter.hasNext()) {
                String preq = iter.next();
                Technology reqTech = technologies.get(preq);
                if(reqTech.is_start_tech && reqTech.area != tech.area) iter.remove();
            }

            for(String preq : tech.prerequisites) {
                Technology reqTech = technologies.get(preq);
                HashMap<String,String> item = new HashMap<>();
                item.put("key", reqTech.key);
                item.put("name", reqTech.name);
                tech.prerequisites_names.add(item);
            }

            tech.base_weight = tech.base_weight*tech.base_factor;

            // Flag Event Techs
            if(tech.base_weight == 0 && tech.prerequisites.size() < 1 && !tech.is_start_tech) tech.is_event = true;
            if(tech.base_weight == 0 && !tech.key.equals("tech_colossus") && !tech.key.equals("tech_mine_living_metal") && !tech.is_start_tech) tech.is_event = true;
            if(tech.base_weight > 0 && tech.weight_modifiers.size() > 0 && tech.weight_modifiers.get(0).type == ModifierType.always && tech.weight_modifiers.get(0).factor == 0.0f) tech.is_event = true;

            // Re-order prerequisite so the most costly is first AND must be the same AREA
            tech.prerequisites.sort((o1, o2) -> {
                Technology parent1 = technologies.get(o1);
                Technology parent2 = technologies.get(o2);

                // Same AREA - will compare key - are they similar ? ie. tech_energy_lance_1 vs tech_energy_lance_2
                String key1 = parent1.key.replaceAll("\\d","");
                String key2 = tech.key.replaceAll("\\d","");

                if(key1.equals(key2)) return -1;

                // Same AREA - will compare Costs
                if(parent1.area.equals(tech.area) && parent2.area.equals(tech.area)) {
                    return parent1.cost.compareTo(parent2.cost);
                }
                // Not same AREA - Will prioritize the one the same as child tech
                if(parent1.area.equals(tech.area) && !parent2.area.equals(tech.area)) return -1;
                if(!parent1.area.equals(tech.area) && parent2.area.equals(tech.area)) return 1;

                return 0;
            });

            for(Modifier m : tech.potential) {
                if(m.type.equals(ModifierType.is_gestalt)) {
                    if(gs(m.pair).equals("yes")) tech.is_gestalt = true;
                    else tech.is_gestalt = false;
                }
                if(m.type.equals(ModifierType.is_megacorp)) {
                    if(gs(m.pair).equals("yes")) tech.is_megacorp = true;
                    else tech.is_megacorp = false;
                }
                if(m.type.equals(ModifierType.is_machine_empire)) {
                    if(gs(m.pair).equals("yes")) tech.is_machine_empire = true;
                    else tech.is_machine_empire = false;
                }
                if(m.type.equals(ModifierType.is_hive_empire)) {
                    if(gs(m.pair).equals("yes")) tech.is_hive_empire = true;
                    else tech.is_hive_empire = false;
                }
                String str = m.toString();
                if(str.contains("Machine Intelligence Authority")) {
                    if(str.contains(" NOT have Machine Intelligence Authority")) {
                        tech.is_machine_empire = false;
                    } else {
                        tech.is_machine_empire = true;
                    }
                    if(str.contains("Has Government Civic: Driven Assimilator")) {
                        tech.is_drive_assimilator = true;
                    }
                    if(str.contains("Has Government Civic: Rogue Servitor")) {
                        tech.is_rogue_servitor = true;
                    }
                } else if (str.contains("Gestalt Consciousness Ethic")) {
                    if(str.contains(" NOT ")) {
                        tech.is_gestalt = false;
                    } else {
                        tech.is_gestalt = true;
                    }
                } else if (str.contains("Hive Mind Authority")) {
                    if(str.contains(" NOT ")) {
                        tech.is_hive_empire = false;
                    } else {
                        tech.is_hive_empire = true;
                    }
                }
            }

            tech.weight_rules = RuleBuilder.buildWeightRules(tech.weight_modifiers);
            tech.potential_rules = RuleBuilder.buildPotentialRules(tech.potential);
        });

        technologies.values().stream().filter(tech -> tech.prerequisites.size()> 0).forEach(tech -> {
            if(tech.is_event) return;
            Technology parent = technologies.get(tech.prerequisites.get(0));
            // Event techs are shown as a flat list - never insert pseudo nodes under them
            if(parent.is_event) parent.children.add(tech);
            else attach(parent, tech, parent.tier == null ? 0 : parent.tier);
        });
    }

    // Maps a rule "fact" name to its empire_options category. Facts absent here
    // (has_technology, is_gestalt, always_false, ...) are not cataloged.
    private static final Map<String,String> FACT_CATEGORY = new LinkedHashMap<>();
    static {
        FACT_CATEGORY.put("has_ethic", "ethics");
        FACT_CATEGORY.put("has_authority", "authorities");
        FACT_CATEGORY.put("has_civic", "civics");
        FACT_CATEGORY.put("has_origin", "origins");
        FACT_CATEGORY.put("has_tradition", "traditions");
        FACT_CATEGORY.put("has_trait_in_council", "council_traits");
        FACT_CATEGORY.put("has_ascension_perk", "ascension_perks");
        FACT_CATEGORY.put("host_has_dlc", "dlcs");
    }

    public void writeEmpireOptions(Map<String,Technology> technologies) throws IOException {
        Map<String,Set<String>> collected = new LinkedHashMap<>();
        for(String cat : FACT_CATEGORY.values()) collected.put(cat, new HashSet<>());

        for(Technology tech : technologies.values()) {
            for(JsonElement rule : tech.weight_rules) {
                if(rule.isJsonObject() && rule.getAsJsonObject().has("if")) {
                    collect(rule.getAsJsonObject().get("if"), collected);
                }
            }
            for(JsonElement rule : tech.potential_rules) {
                collect(rule, collected);
            }
        }

        JsonObject root = new JsonObject();
        for(Map.Entry<String,String> e : FACT_CATEGORY.entrySet()) {
            String cat = e.getValue();
            List<JsonObject> entries = new ArrayList<>();
            for(String key : collected.get(cat)) {
                JsonObject o = new JsonObject();
                o.addProperty("key", key);
                o.addProperty("name", displayName(cat, key));
                entries.add(o);
            }
            entries.sort(Comparator.comparing(o -> o.get("name").getAsString(), String.CASE_INSENSITIVE_ORDER));
            JsonArray arr = new JsonArray();
            entries.forEach(arr::add);
            root.add(cat, arr);
        }

        Gson pretty = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        FileOutputStream fos = new FileOutputStream("output/empire_options.json");
        fos.write(pretty.toJson(root).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        fos.close();
    }

    private static String displayName(String category, String key) {
        String name;
        if(category.equals("council_traits")) name = traitName(key);
        else name = i18n(key);
        if(name.equals(key)) {
            String suffixed = i18n(key + "_name");
            if(!suffixed.equals(key + "_name")) name = suffixed;
        }
        // Data-function names ([GetChosenName]) cannot be resolved statically - prettify the key
        if(name.contains("[")) {
            name = org.apache.commons.lang3.StringUtils.capitalize(
                    name.equals(key) ? key : key.replaceAll("^(leader_trait_|trait_)", "").replace('_', ' '));
        }
        return name;
    }

    private static void collect(JsonElement el, Map<String,Set<String>> collected) {
        if(el == null || !el.isJsonObject()) return;
        JsonObject o = el.getAsJsonObject();
        if(o.has("fact") && o.has("value")) {
            String cat = FACT_CATEGORY.get(o.get("fact").getAsString());
            if(cat != null) collected.get(cat).add(o.get("value").getAsString());
        }
        for(String op : new String[]{"any","all","none"}) {
            if(o.has(op) && o.get(op).isJsonArray()) {
                for(JsonElement child : o.get(op).getAsJsonArray()) collect(child, collected);
            }
        }
    }

    public abstract void read(String folder) throws IOException;
}
