package org.dantesys.reliquiasNexus.bosses;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BossNames {

    private static final Random random = new Random();

    // Nomes para bosses Comuns
    private static final List<String> COMMON_NAMES = Arrays.asList(
            "Zumbi Rançoso", "Esqueleto Decrépito", "Aranha Venenosa", "Creeper Antigo", "Fantasma Sombrio",
            "Lobisomem Fraco", "Goblin Pequeno", "Orc Inferior", "Troll das Cavernas", "Morcego Gigante",
            "Slime Verde", "Zumbi do Pântano", "Esqueleto Armado", "Aranha da Noite", "Creeper do Abismo",
            "Fantasma do Passado", "Lobisomem Jovem", "Goblin Ladrão", "Orc Guerreiro", "Troll da Montanha",
            "Morcego Sanguinário", "Slime Azul", "Zumbi do Deserto", "Esqueleto Arqueiro", "Aranha da Floresta",
            // ... continue com 225 nomes adicionais
            "Demônio Menor", "Serpente das Trevas", "Dragãozinho", "Fera da Noite", "Assombração"
    );

    // Nomes para bosses Incomuns
    private static final List<String> UNCOMMON_NAMES = Arrays.asList(
            "Cavaleiro das Sombras", "Necromante Júnior", "Bruxo do Pantano", "Golem de Pedra", "Quimera Jovem",
            "Hidra de Duas Cabeças", "Minotauro Adolescente", "Sereia Traiçoeira", "Centauro Guerreiro", "Sátiro Dançarino",
            "Elemental de Terra", "Elemental de Água", "Elemental de Fogo", "Elemental de Ar", "Fênix Renascida",
            "Grifo Alado", "Basilisco Jovem", "Medusa Aprendiz", "Ciclope Solitário", "Harpias do Vento",
            // ... continue com 230 nomes adicionais
            "Lich Aprendiz", "Vampiro Noturno", "Lobisomem Alfa", "Goblin Xamã", "Orc Chefe"
    );

    // Nomes para bosses Raros
    private static final List<String> RARE_NAMES = Arrays.asList(
            "Dragão das Tempestades", "Fênix Eternal", "Leviatã dos Mares", "Behemoth da Montanha", "Kraken Abissal",
            "Cavaleiro da Morte", "Arcanho Supremo", "Deus Menor da Guerra", "Titã do Caos", "Serafim Caído",
            "Demônio Ancestral", "Anjo Vingativo", "Elemental Primordial", "Golem de Mithril", "Quimera Ancestral",
            "Hidra de Cinco Cabeças", "Minotauro Rei", "Sereia Rainha", "Centauro Lendário", "Sátiro Encantador",
            // ... continue com 230 nomes adicionais
            "Necromante Mestre", "Bruxo Supremo", "Xamã Tribal", "Guardião Antigo", "Protetor Sagrado"
    );

    // Nomes para bosses Épicos
    private static final List<String> EPIC_NAMES = Arrays.asList(
            "Dragão Ancestral", "Fênix Solar", "Leviatã Primordial", "Behemoth Lendário", "Kraken Titanico",
            "Cavaleiro do Apocalipse", "Arcanho Celestial", "Deus da Guerra", "Titã do Destino", "Serafim Divino",
            "Demônio Arquisimples", "Anjo Celestial", "Elemental Cósmico", "Golem Divino", "Quimera Mítica",
            "Hidra de Nove Cabeças", "Minotauro Divino", "Sereia Divina", "Centauro Celestial", "Sátiro Divino",
            // ... continue com 230 nomes adicionais
            "Lich Rei", "Vampiro Ancestral", "Lobisomem Lendário", "Goblin Divino", "Orc Titanico"
    );

    // Nomes para bosses Lendários
    private static final List<String> LEGENDARY_NAMES = Arrays.asList(
            "Dragão Celestial", "Fênix Cósmica", "Leviatã Cósmico", "Behemoth Cósmico", "Kraken Cósmico",
            "Cavaleiro do Vácuo", "Arcanho Divino", "Deus Supremo", "Titã Cósmico", "Serafim Supremo",
            "Demônio Supremo", "Anjo Supremo", "Elemental Universal", "Golem Cósmico", "Quimera Cósmica",
            "Hidra Cósmica", "Minotauro Cósmico", "Sereia Cósmica", "Centauro Cósmico", "Sátiro Cósmico",
            // ... continue com 230 nomes adicionais
            "Lich Cósmico", "Vampiro Cósmico", "Lobisomem Cósmico", "Goblin Cósmico", "Orc Cósmico"
    );

    public static String getRandomName(BossRarity rarity) {
        switch (rarity) {
            case COMMUN:
                return COMMON_NAMES.get(random.nextInt(COMMON_NAMES.size()));
            case INCOMMON:
                return UNCOMMON_NAMES.get(random.nextInt(UNCOMMON_NAMES.size()));
            case RARE:
                return RARE_NAMES.get(random.nextInt(RARE_NAMES.size()));
            case EPIC:
                return EPIC_NAMES.get(random.nextInt(EPIC_NAMES.size()));
            case LEGENDARY:
                return LEGENDARY_NAMES.get(random.nextInt(LEGENDARY_NAMES.size()));
            default:
                return "Boss Desconhecido";
        }
    }
}