-- DEU, ENG // ISO 639-3
DROP TABLE IF EXISTS Lang;
CREATE TABLE Lang (
    lang3 CHAR(3) NOT NULL,
    created DATETIME(6) DEFAULT NULL, -- with fractional part http://dev.mysql.com/doc/refman/5.7/en/fractional-seconds.html
    createdby VARCHAR(255) DEFAULT NULL,
    updated DATETIME(6) DEFAULT NULL, -- with fractional part
    updatedby VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY(lang3)
);

-- Trap, Arcane, Equipment, Aura, Human, Rat, Squirrel
DROP TABLE IF EXISTS SubType;
CREATE TABLE SubType (
    id INT(10) NOT NULL AUTO_INCREMENT,
    PRIMARY KEY(id)
);

DROP TABLE IF EXISTS SubTypeLang;
CREATE TABLE SubTypeLang (
    name VARCHAR(255) NOT NULL,
    subType_id INT(10) NOT NULL,
    lang_lang3 CHAR(3) NOT NULL,
    FOREIGN KEY(subType_id) REFERENCES SubType(id),
    FOREIGN KEY(lang_lang3) REFERENCES Lang(lang3),
    UNIQUE(name, lang_lang3),
    PRIMARY KEY(subType_id, lang_lang3)
);

-- Sorcery, Artifact, Creature, Enchantment, Land, Planeswalker
DROP TABLE IF EXISTS CardType;
CREATE TABLE CardType (
    id INT(10) NOT NULL AUTO_INCREMENT,
    PRIMARY KEY(id)
);

DROP TABLE IF EXISTS CardTypeLang;
CREATE TABLE CardTypeLang (
    name VARCHAR(255) NOT NULL,
    cardType_id INT(10) NOT NULL,
    lang_lang3 CHAR(3) NOT NULL,
    FOREIGN KEY(cardType_id) REFERENCES CardType(id),
    FOREIGN KEY(lang_lang3) REFERENCES Lang(lang3),
    PRIMARY KEY(cardType_id, lang_lang3)
);

-- Basic, Legendary, Snow, World, Ongoing
DROP TABLE IF EXISTS SuperType;
CREATE TABLE SuperType (
    id INT(10) NOT NULL AUTO_INCREMENT,
    PRIMARY KEY(id)
);

DROP TABLE IF EXISTS SuperTypeLang;
CREATE TABLE SuperTypeLang (
    name VARCHAR(255) NOT NULL,
    superType_id INT(10) NOT NULL,
    lang_lang3 CHAR(3) NOT NULL,
    FOREIGN KEY(superType_id) REFERENCES SuperType(id),
    FOREIGN KEY(lang_lang3) REFERENCES Lang(lang3),
    PRIMARY KEY(superType_id, lang_lang3)
);

DROP TABLE IF EXISTS Artist;
CREATE TABLE Artist (
    id INT(10) NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY(id)
);

DROP TABLE IF EXISTS CardSet;
CREATE TABLE CardSet (
    code CHAR(10) NOT NULL,
    type VARCHAR(20) NOT NULL, -- core, expansion, reprint, box, un
    border VARCHAR(20) NOT NULL, -- black, silver
    totalCount INT(3) DEFAULT 0,
    releasedAt DATE,
    PRIMARY KEY(code)
);

DROP TABLE IF EXISTS CardSetLang;
CREATE TABLE CardSetLang (
    name VARCHAR(50) NOT NULL,
    block VARCHAR(50) NOT NULL, -- Masques
    description VARCHAR(255),
    cardSet_code CHAR(10) NOT NULL,
    lang_lang3 CHAR(3) NOT NULL,
    FOREIGN KEY(cardSet_code) REFERENCES CardSet(code),
    FOREIGN KEY(lang_lang3) REFERENCES Lang(lang3),
    PRIMARY KEY(cardSet_code, lang_lang3)
);

DROP TABLE IF EXISTS Card;
CREATE TABLE Card (
    id BIGINT(10) NOT NULL,
    cost VARCHAR(50), -- {G}{U} or G,U?
    color CHAR(10) NOT NULL, -- Blue, Green
    cmc INT(2) DEFAULT 0,
    power CHAR(4) DEFAULT '0', -- 1+*
    toughness CHAR(4) DEFAULT '0', -- 1+*
    handModifier INT(2),
    lifeModifier INT(2),
    loyalty INT(2),
    relatedCardIds VARCHAR(255), -- 67239,18239,21939
    number CHAR(4) NOT NULL,
    rarity VARCHAR(100) NOT NULL, -- Common, Uncommon, Rare, Mythic Rare, Special, Basic Land
    layout VARCHAR(100) NOT NULL, -- normal, split, flip, double-faced, token, plane, scheme, phenomenon, leveler, vanguard
    border VARCHAR(20) NOT NULL, -- black, silver
    artist_id INT(10) NOT NULL,
    cardSet_code CHAR(10) NOT NULL,
    FOREIGN KEY(cardSet_code) REFERENCES CardSet(code),
    FOREIGN KEY(artist_id) REFERENCES Artist(id),
    UNIQUE(id, cardSet_code),
    PRIMARY KEY(id)
);

DROP TABLE IF EXISTS CardLang;
CREATE TABLE CardLang (
    name VARCHAR(200) NOT NULL,
    multiverseId INT(10) NOT NULL,
    flavor VARCHAR(255),
    card_id BIGINT(10) NOT NULL,
    lang_lang3 CHAR(3) NOT NULL,
    FOREIGN KEY(card_id) REFERENCES Card(id),
    FOREIGN KEY(lang_lang3) REFERENCES Lang(lang3),
    PRIMARY KEY(card_id, lang_lang3)
);

DROP TABLE IF EXISTS CardText;
CREATE TABLE CardText (
    id INT(10) NOT NULL,
    type CHAR(10) NOT NULL, -- spell, Trigger, Static, activate
    keyword VARCHAR(255) NOT NULL, -- Attach
    card_ID BIGINT(10) NOT NULL,
    FOREIGN KEY(card_id) REFERENCES Card(id),
    PRIMARY KEY(id)
);

DROP TABLE IF EXISTS CardTextLang;
CREATE TABLE CardTextLang (
    id INT(10) NOT NULL,
    text VARCHAR(500) NOT NULL,
    cardText_id INT(10) NOT NULL,
    lang_lang3 CHAR(3) NOT NULL,
    FOREIGN KEY(lang_lang3) REFERENCES Lang(lang3),
    FOREIGN KEY(cardText_id) REFERENCES CardText(id),
    PRIMARY KEY(id)
);

DROP TABLE IF EXISTS Rule;
CREATE TABLE Rule (
  id INT(10) NOT NULL,
  text VARCHAR(500) NOT NULL,
  releasedAt Date NOT NULL,
  PRIMARY KEY(id)
);

DROP TABLE IF EXISTS CardRule;
CREATE TABLE CardRule (
  rule_id INT(10) NOT NULL,
  card_id BIGINT(10) NOT NULL,
  FOREIGN KEY(card_id) REFERENCES Card(id),
  FOREIGN KEY(rule_id) REFERENCES Rule(id)
);

DROP TABLE IF EXISTS Card_CardType;
CREATE TABLE Card_CardType (
  card_id BIGINT(10) NOT NULL,
  cardType_id INT(10) NOT NULL,
  FOREIGN KEY(card_id) REFERENCES Card(id),
  FOREIGN KEY(cardType_id) REFERENCES CardType(id)
);

DROP TABLE IF EXISTS Card_SubType;
CREATE TABLE Card_SubType (
  card_id BIGINT(10) NOT NULL,
  subType_id INT(10) NOT NULL,
  FOREIGN KEY(card_id) REFERENCES Card(id),
  FOREIGN KEY(subType_id) REFERENCES SubType(id)
);

DROP TABLE IF EXISTS Card_SuperType;
CREATE TABLE Card_SuperType (
  card_id BIGINT(10) NOT NULL,
  superType_id INT(10) NOT NULL,
  FOREIGN KEY(card_id) REFERENCES Card(id),
  FOREIGN KEY(superType_id) REFERENCES SuperType(id)
);

