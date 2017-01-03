### Language
lang3 (DE, EN), unique


### SubType
id

### SubTypeLang
name (Trap, Arcane, Equipment, Aura, Human, Rat, Squirrel, ...)
subType_Id
lang_lang3


### CardType
name (Sorcery, Artifact, Creature, Enchantment, Land, Planeswalker, ...)


### SuperType
name (Basic, Legendary, Snow, World, Ongoing ...)


### Artist
name


### Card set
code (NMS, ARN, ...)

name (Nemesis, Arabian Nights) // Translate

description ??

block (Masques, ...)

type (core, expansion, reprint, box, un) // Static

border (black, silver) // Kann bei einzelnen Karten des Sets unterschiedlich sein // Static

totalCount

releasedAt


### Card set lang
name

description

block

lang_lang3



### Card
name // Translate

cost ({G}{U})

color (Blue, Green)

cmc (Converted mana cost)

text ?? // Text kommt aus Card_Text // Translate

power (1+*)

toughness (1+*)

handModifier (only Vanguard cards)

lifeModifier (only Vanguard cards)

loyalty

multiverseId (Id aus gatherer.wizards.com) // Anders bei anderer Sprache

relatedCardIds (For split, double-faced and meld cards)

number (148a)

flavor // Translate

rarity (Common, Uncommon, Rare, Mythic Rare, Special, Basic Land) // Static

layout (normal, split, flip, double-faced, token, plane, scheme, phenomenon, leveler, vanguard, meld) // Static

border (black, silver)

artist_name

cardSet_code

releasedAt (Only for promo cards?)


### Card_Lang

name

multiverseId

flavor



Vorschlag Textbausteine für den Card Text
### Card text
text // Translate
type (Spell, Trigger, Static, activate, ...) // Static
keyword (Attach, ...) // Static


### Card_Text_Lang

text


### Rule
text
releasedAt



# Rest api

Api prefix = /api/v1

### Crud
GET     /{modelName} Return all defined entities
PUT     /{modelName}/{id} Update entity with specific id
POST    /{modelName}/ Create new entity
DELETE  /{modelName}/{id} Delete entity by id

### Ideas

* HATEOS
* GraphQL
* Spring Data Projection? Or map db result directly into json with JOOQ?

* How to handle errors? e.g. Duplicate entry, entry not found

* Mock data access layer?

```
Subtype {
    id:
    lang:
    name:
}


Card {
    id: 1,
    cost: 10,
    color: "blue",
    lang: "DE",
    ..
    artist: {
        id: 5,
        name: "Susi"
    }

    cardset: {
        name: "Nxxxx Exxx",
        block: "",
        description: "",
        lang: "DE",
        code: "NE",
        type: "",
        "border": ",
        ...
    }


}
```
