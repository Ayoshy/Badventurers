# Gameplay Screen Loop

Last updated: 2026-06-22

Ce document complète `08-ux-flow.md` avec une vue graphique de la boucle de gameplay et des écrans prévus ensuite. Il distingue les écrans déjà présents dans le prototype Compose des écrans futurs attendus pour le MVP et les phases suivantes.

## Graphique

![Gameplay screen loop](diagrams/gameplay-screen-loop.svg)

## Lecture rapide

- Actuel: écran, onglet ou état déjà présent dans l'application Android.
- Futur MVP: écran nécessaire pour rendre la boucle répétable et plus claire.
- Futur long terme: écran prévu pour la monétisation, la rétention ou la progression avancée.

La boucle principale doit rester simple: ouvrir la guilde, récupérer le résultat, améliorer quelque chose, lancer une nouvelle expédition, puis revenir plus tard.

## Diagramme éditable

```mermaid
flowchart TD
    classDef current fill:#2F695C,stroke:#FFF1C0,color:#FFF1C0,stroke-width:2px
    classDef currentState fill:#32483F,stroke:#D0A24A,color:#FFF1C0,stroke-width:2px
    classDef futureMvp fill:#FFF1BF,stroke:#D0A24A,color:#211F1A,stroke-width:2px
    classDef futureLong fill:#E8D7A0,stroke:#7B5531,color:#211F1A,stroke-width:2px,stroke-dasharray:6 4

    FirstLaunch["Premier lancement\nFUTUR MVP"]:::futureMvp
    Language["Choix langue\nFUTUR MVP"]:::futureMvp
    GuildName["Nommer la guilde\nFUTUR MVP"]:::futureMvp
    Tutorial["Tutoriel: première quête\nFUTUR MVP"]:::futureMvp

    Guild["Guild Home\nACTUEL"]:::current
    Quests["Quests\nACTUEL"]:::current
    Prep["Expedition Prep\nFUTUR MVP"]:::futureMvp
    Active["Expedition Active\nACTUEL: état Guild"]:::currentState
    Timer["App fermée / timer idle\nFUTUR MVP"]:::futureMvp
    Return["Retour session\nFUTUR MVP"]:::futureMvp
    Offline["Offline Summary\nFUTUR MVP"]:::futureMvp
    Ready["Résultat prêt\nACTUEL: état Guild"]:::currentState
    Result["Quest Result dédié\nFUTUR MVP"]:::futureMvp
    Reward["Reward Choice\nFUTUR MVP"]:::futureMvp
    Ad["Rewarded Ad Prompt\nFUTUR MVP"]:::futureMvp

    Loot["Loot\nACTUEL"]:::current
    Item["Item Detail\nFUTUR MVP"]:::futureMvp
    Equip["Equip Item\nFUTUR MVP"]:::futureMvp
    Heroes["Heroes\nACTUEL"]:::current
    HeroDetail["Hero Detail\nFUTUR MVP"]:::futureMvp
    Upgrades["Upgrades\nACTUEL"]:::current
    Facilities["Facilities avancées\nFUTUR MVP"]:::futureMvp

    Shop["Shop\nFUTUR long terme"]:::futureLong
    Settings["Settings\nFUTUR MVP"]:::futureMvp
    Events["Events / seasonal quests\nFUTUR long terme"]:::futureLong
    Prestige["Prestige / charter bonuses\nFUTUR long terme"]:::futureLong
    Recruit["Recruitment / promotion\nFUTUR MVP"]:::futureMvp

    FirstLaunch --> Language --> GuildName --> Tutorial --> Guild

    Guild --> Quests --> Prep --> Active --> Timer --> Return --> Offline --> Ready --> Result --> Reward
    Reward --> Loot --> Item --> Equip --> HeroDetail --> Heroes --> Guild
    Reward --> Ad --> Reward

    Guild --> Heroes
    Guild --> Loot
    Guild --> Upgrades --> Facilities --> Guild
    Guild --> Shop --> Guild
    Guild --> Settings --> Language

    Events --> Quests
    Heroes --> Recruit --> Heroes
    Prestige --> Guild
```

## Écrans à garder dans le scope

| Priorité | Écran | Statut | Rôle dans la boucle |
| --- | --- | --- | --- |
| 1 | Guild Home | Actuel | Hub de retour, ressources, expédition active, journal récent. |
| 2 | Quests | Actuel | Choix de la prochaine expédition. |
| 3 | Expedition Prep | Futur MVP | Choix du groupe, estimation de succès, lancement clair. |
| 4 | Expedition Active | Actuel comme état Guild | Timer visible et promesse de retour. |
| 5 | Offline Summary | Futur MVP | Récompense le joueur qui revient après absence. |
| 6 | Quest Result | Futur MVP | Payoff lisible: outcome, journal, loot, complications. |
| 7 | Reward Choice | Futur MVP | Collecter, inspecter, éventuellement doubler une récompense. |
| 8 | Loot / Item Detail / Equip Item | Actuel puis Futur MVP | Transformer le résultat en amélioration concrète. |
| 9 | Heroes / Hero Detail | Actuel puis Futur MVP | Comprendre et optimiser le groupe. |
| 10 | Upgrades / Facilities | Actuel puis Futur MVP | Dépenser l'or et créer des objectifs longs. |
| 11 | Settings / Shop | Futur | Support, langue, achats, pubs optionnelles. |
| 12 | Events / Prestige | Futur long terme | Rétention, contenu saisonnier, progression avancée. |
