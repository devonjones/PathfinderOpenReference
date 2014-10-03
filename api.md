Intro
=====

Pathfinder Open Reference provides an android API that can be used from other android projects.  The bulk of the api is in the form of Intents and Content Providers.  To use the Content Providers, you need to add the [contracts jar](https://github.com/devonjones/PathfinderOpenReference/raw/master/tools/pfor-contracts.jar) to your project.

I have also created a [sample toy app](https://github.com/devonjones/PathfinderOpenReferenceApiTest) that can use the api as an example.

Note: I intend in the next release of Pathfinder Open Reference to provide a Content Provider that will inform you of the api version.  I strongly suggest not releasing any application until that is in place and integrated with your app so you can be certain the user has a version of Pathfinder Open Reference that works with your application.


Intents
=======

Launch an article:

    Intent intent = new Intent("android.intent.action.MAIN");
    intent.setComponent(ComponentName.unflattenFromString("org.evilsoft.pathfinder.reference/org.evilsoft.pathfinder.reference.DetailsActivity"));
    intent.setData(Uri.parse(url)); // Url should be a content url from either supportedurls.txt, or the content_url field from any Content Provider
    intent.addCategory("android.intent.category.LAUNCHER");
    startActivity(intent);


Do a search:

    Intent intent = new Intent("android.intent.action.MAIN");
    intent.setComponent(ComponentName.unflattenFromString("org.evilsoft.pathfinder.reference/org.evilsoft.pathfinder.reference.DetailsActivity"));
    intent.setAction(Intent.SEACH_ACTION);
    intent.putExtra(SearchManager.QUERY, query); // Query should be the string being searched for.
    intent.addCategory("android.intent.category.LAUNCHER");
    startActivity(intent);

Content Providers
=================

SectionContentProvider
----------------------

### Search
> content://org.evilsoft.pathfinder.reference.api.section/sections
> vnd.android.cursor.dir/org.evilsoft.pathfinder.reference.api.section.list

#### Columns
* source
* type
* subtype
* name
* description
* content_url

### Content
> content://org.evilsoft.pathfinder.reference.api.section/sections/#id#.html
> text/html

CreatureContentProvider
-----------------------

### Search
> content://org.evilsoft.pathfinder.reference.api.creature/creatures
> vnd.android.cursor.dir/org.evilsoft.pathfinder.reference.api.creatures.list

#### Columns
* source
* type
* subtype
* name
* description
* content_url
* creature_type
* creature_subtype
* super_race
* cr
* xp
* size
* alignment

### Content
> content://org.evilsoft.pathfinder.reference.api.creature/creatures/#id#.html
> text/html

FeatContentProvider
-------------------

### Search
> content://org.evilsoft.pathfinder.reference.api.feat/feats
> vnd.android.cursor.dir/org.evilsoft.pathfinder.reference.api.feat.list

#### Columns
* source
* type
* subtype
* name
* description
* content_url
* feat_types
* prerequisites

### Content
> content://org.evilsoft.pathfinder.reference.api.feat/feats/#id#.html
> text/html

SkillContentProvider
--------------------

### Search
> content://org.evilsoft.pathfinder.reference.api.skill/skills
> vnd.android.cursor.dir/org.evilsoft.pathfinder.reference.api.skill.list

#### Columns
* source
* type
* subtype
* name
* description
* content_url
* attribute
* armor_check_penalty
* trained_only

### Content
> content://org.evilsoft.pathfinder.reference.api.skill/skills/#id#.html
> text/html

SpellContentProvider
--------------------

### Search
> content://org.evilsoft.pathfinder.reference.api.spell/spells
> vnd.android.cursor.dir/org.evilsoft.pathfinder.reference.api.class.list

-or-

> content://org.evilsoft.pathfinder.reference.api.spell/spells/filtered
> vnd.android.cursor.dir/org.evilsoft.pathfinder.reference.api.class.list

#### Columns
* source
* type
* subtype
* name
* description
* content_url
* school
* subschool
* descriptor
* classes
* components

-or-

> content://org.evilsoft.pathfinder.reference.api.spell/class/#id#/spells
> vnd.android.cursor.dir/org.evilsoft.pathfinder.reference.api.class.spell.list

-or-

> content://org.evilsoft.pathfinder.reference.api.spell/class/#id#/spells/filtered
> vnd.android.cursor.dir/org.evilsoft.pathfinder.reference.api.class.spell.list

#### Columns
* source
* type
* subtype
* name
* description
* content_url
* class
* level
* magic_type
* school
* subschool
* descriptor
* components

### Content
> content://org.evilsoft.pathfinder.reference.api.spell/spells/#id#.html
> text/html


Data Dictionary
===============
This is a (mostly) complete listing of the possible values for various fields to help you with your queries.

Source
------
* Advanced Player's Guide
* Advanced Race Guide
* Bestiary
* Bestiary 2
* Bestiary 3
* Core Rulebook
* Game Mastery Guide
* Mythic Adventures
* NPC Codex
* Ultimate Campaign
* Ultimate Combat
* Ultimate Equipment
* Ultimate Magic

Type
----
* ability
* affliction
* animal_companion
* army
* class
* class_archetype
* creature
* drawback
* embed
* feat
* haunt
* item
* kingdom_resource
* mythic_spell
* race
* racial_trait
* resource
* section
* settlement
* skill
* spell
* table
* trait
* trap
* vehicle

Subtype
-------
* aasimar
* addiction
* advancement
* alchemist
* alchemist_discovery
* arcane_school
* armor
* arms
* barbarian
* barbarian_rage_power
* bard
* bard_masterpiece
* bardic_performance
* base
* belt
* bloodline
* body
* building
* campaign
* catfolk
* cavalier
* changeling
* chest
* cleric
* cleric_domain
* cleric_subdomain
* cleric_variant_channeling
* combat
* combat_maneuver
* condition
* core
* core_race
* creature_type
* curse
* dhampir
* disease
* drow
* druid
* druid_domain
* duergar
* dwarf
* dwarves
* elemental_arcane_school
* elf
* elves
* eyes
* faith
* familiar
* featured_race
* feet
* fetchling
* fighter
* focused_arcane_school
* gillman
* gnome
* gnomes
* goblin
* grippli
* gunslinger
* gunslinger_deed
* half-elves
* half-orcs
* half_elf
* half_orc
* halfling
* halflings
* hands
* head
* headband
* hobgoblin
* human
* humans
* ifrit
* infestation
* inquisitor
* inquisitor_inquisition
* insanity
* kitsune
* kobold
* magic
* magus
* magus_arcana
* manager
* merfolk
* monk
* monk_vow
* monster_race
* mythic
* mythic_spell
* nagaji
* neck
* ninja_trick
* npc
* oracle
* oracle_mystery
* orc
* oread
* organization
* paladin
* poison
* prestige
* racial_dwarf
* racial_elf
* racial_gnome
* racial_half_elf
* racial_half_orc
* racial_halfling
* racial_human
* racial_orc
* ranger
* ranger_combat_style
* ranger_trap
* ratfolk
* region
* regional
* religion
* ring
* rogue
* rogue_advanced_talent
* rogue_talent
* room
* room_augmentation
* samsaran
* shield
* shoulders
* social
* sorcerer
* sorcerer_bloodline
* special_abilities
* spell_list
* spellbook
* standard_race
* strix
* suli
* summoner
* summoner_evolution_1
* summoner_evolution_2
* summoner_evolution_3
* summoner_evolution_4
* svirfneblin
* sylph
* team
* template
* tengu
* tiefling
* uncommon_race
* undine
* vanara
* vishkanya
* warrior_order
* wayang
* witch
* witch_grand_hex
* witch_hex
* witch_major_hex
* witch_patron
* wizard
* wrist

Type|Subtype
------------
* ability|
* ability|alchemist_discovery
* ability|barbarian_rage_power
* ability|bardic_performance
* ability|gunslinger_deed
* ability|magus_arcana
* ability|ninja_trick
* ability|rogue_advanced_talent
* ability|rogue_talent
* ability|summoner_evolution_1
* ability|summoner_evolution_2
* ability|summoner_evolution_3
* ability|summoner_evolution_4
* ability|witch_grand_hex
* ability|witch_hex
* ability|witch_major_hex
* affliction|addiction
* affliction|curse
* affliction|disease
* affliction|infestation
* affliction|insanity
* affliction|poison
* animal_companion|advancement
* animal_companion|base
* army|
* class|base
* class|core
* class|npc
* class|prestige
* class_archetype|alchemist
* class_archetype|barbarian
* class_archetype|bard
* class_archetype|cavalier
* class_archetype|cleric
* class_archetype|druid
* class_archetype|fighter
* class_archetype|gunslinger
* class_archetype|inquisitor
* class_archetype|magus
* class_archetype|monk
* class_archetype|oracle
* class_archetype|paladin
* class_archetype|ranger
* class_archetype|rogue
* class_archetype|sorcerer
* class_archetype|summoner
* class_archetype|witch
* class_archetype|wizard
* creature|
* creature|familiar
* creature|mythic
* creature|npc
* drawback|
* embed|spell_list
* feat|
* haunt|
* item|
* item|armor
* item|arms
* item|belt
* item|body
* item|chest
* item|eyes
* item|feet
* item|hands
* item|head
* item|headband
* item|neck
* item|ring
* item|shield
* item|shoulders
* item|wrist
* kingdom_resource|
* mythic_spell|
* mythic_spell|mythic_spell
* race|core_race
* race|featured_race
* race|monster_race
* race|standard_race
* race|uncommon_race
* racial_trait|aasimar
* racial_trait|catfolk
* racial_trait|changeling
* racial_trait|dhampir
* racial_trait|drow
* racial_trait|duergar
* racial_trait|dwarf
* racial_trait|dwarves
* racial_trait|elf
* racial_trait|elves
* racial_trait|fetchling
* racial_trait|gillman
* racial_trait|gnome
* racial_trait|gnomes
* racial_trait|goblin
* racial_trait|grippli
* racial_trait|half-elves
* racial_trait|half-orcs
* racial_trait|half_elf
* racial_trait|half_orc
* racial_trait|halfling
* racial_trait|halflings
* racial_trait|hobgoblin
* racial_trait|human
* racial_trait|humans
* racial_trait|ifrit
* racial_trait|kitsune
* racial_trait|kobold
* racial_trait|merfolk
* racial_trait|nagaji
* racial_trait|orc
* racial_trait|oread
* racial_trait|ratfolk
* racial_trait|samsaran
* racial_trait|strix
* racial_trait|suli
* racial_trait|svirfneblin
* racial_trait|sylph
* racial_trait|tengu
* racial_trait|tiefling
* racial_trait|undine
* racial_trait|vanara
* racial_trait|vishkanya
* racial_trait|wayang
* resource|building
* resource|manager
* resource|organization
* resource|room
* resource|room_augmentation
* resource|team
* section|
* section|alchemist_discovery
* section|arcane_school
* section|barbarian_rage_power
* section|bard_masterpiece
* section|cleric_domain
* section|cleric_subdomain
* section|cleric_variant_channeling
* section|combat_maneuver
* section|condition
* section|creature_type
* section|druid_domain
* section|elemental_arcane_school
* section|focused_arcane_school
* section|inquisitor_inquisition
* section|monk_vow
* section|ninja_trick
* section|oracle_mystery
* section|ranger_combat_style
* section|rogue_advanced_talent
* section|rogue_talent
* section|sorcerer_bloodline
* section|special_abilities
* section|spellbook
* section|template
* section|warrior_order
* section|witch_patron
* settlement|
* skill|
* spell|
* spell|mythic_spell
* table|
* trait|bloodline
* trait|campaign
* trait|combat
* trait|faith
* trait|magic
* trait|racial_dwarf
* trait|racial_elf
* trait|racial_gnome
* trait|racial_half_elf
* trait|racial_half_orc
* trait|racial_halfling
* trait|racial_human
* trait|racial_orc
* trait|region
* trait|regional
* trait|religion
* trait|social
* trap|
* trap|ranger_trap
* vehicle|

Creature: Creature Type
-----------------------
* Outsider
* Aberration
* Construct
* Magical Beast
* Animal
* Vermin
* Plant
* Ooze
* Humanoid
* Monstrous Humanoid
* Undead
* Dragon
* Fey

Creature: Creature Subtype
--------------------------
Note: (use creatures/filtered)

Creature: CR
------------
* 1/2
* 1/3
* 1/4
* 1/6
* 1/8
* 1
* 2
* 3
* 4
* 5
* 6
* 7
* 8
* 9
* 10
* 11
* 12
* 13
* 14
* 15
* 16
* 17
* 18
* 19
* 20
* 21
* 22
* 23
* 24
* 25

Creature: XP
------------
* 50
* 65
* 100
* 135
* 200
* 400
* 600
* 800
* 1,200
* 1,600
* 2,400
* 3,200
* 4,800
* 6,400
* 9,600
* 12,800
* 19,200
* 25,600
* 38,400
* 51,200
* 76,800
* 102,400
* 153,600
* 204,800
* 307,200
* 409,600
* 614,400
* 819,200
* 1,228,800
* 1,638,400

Creature: Size
--------------
* Fine
* Diminutive
* Tiny
* Small
* Medium
* Large
* Huge
* Gargantuan
* Colossal

Creature: Alignment
-------------------
* LG
* LN
* LE
* NG
* N
* NE
* CG
* CN
* CE
* NG or NE
* Any alignment (same as creator)
* Any alignment
* N (but see below)

Feat: Feat Types
----------------
Note: (Use feats/filtered)

* Called Shot
* Combat
* Critical
* General
* Grit
* Hero Point
* Item Creation
* Metamagic
* Monster
* Mythic
* Optional
* Performance
* Story
* Style
* Teamwork
* Words of Power

Feat: Feat Prerequisites
------------------------
Note: (Use feats/filtered)

Skill: Attribute
----------------
* Str
* Dex
* Int
* Wis
* Cha

Skill: Armor Check Penalty
--------------------------
* 0
* 1

Skill: Trained Only
-------------------
* 0
* 1

Spell: School
-------------
* abjuration
* conjuration
* divination
* enchantment
* evocation
* illusion
* necromancy
* transmutation
* universal

Spell: Subschool
----------------
Note: (Use spells/filtered or classes/#id#/spells/filtered)

* calling
* charm
* cold
* compulsion
* creation
* electricity
* figment
* force
* glamer
* healing
* light
* pattern
* phantasm
* polymorph
* scrying
* shadow
* summoning
* teleportation


Spell: Descriptor
-----------------
Note: (Use spells/filtered or classes/#id#/spells/filtered)

* acid
* air
* chaos
* chaotic
* cold
* compulsion
* creation
* curse
* darkness
* death
* disease
* earth
* electricity
* emotion
* evil
* fear
* figment
* fire
* force
* good
* language-dependent
* law
* lawful
* light
* mind-affecting
* pain
* poison
* shadow
* sonic
* variable
* water

Spell: Components
-----------------
* DF
* F
* F/DF
* M
* M/DF
* S
* S; see text
* V
* V or F
* V or S

Spell: Class
------------
Note: (Use spells/filtered)

* Adept
* Alchemist
* Antipaladin
* Bard
* Cleric
* Druid
* Elementalist Wizard
* Inquisitor
* Magus
* Oracle
* Paladin
* Ranger
* Sorcerer
* Summoner
* Witch
* Wizard

Spell: Level
------------
Note: (Use spells/filtered)

* 0
* 1
* 2
* 3
* 4
* 5
* 6
* 7
* 8
* 9

Spell: Magic Type
-----------------
Note: (Use spells/filtered)

* arcane
* divine

