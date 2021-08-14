from json import dumps
from enum import Enum


class Card:
    def __init__(self, card_name, mana_cost, type_line, oracle, rulings, card_set, legalities, edhrec_rank, image_url, image_age):
        self.card_name = card_name
        self.mana_cost = mana_cost
        self.type_line = type_line
        self.oracle = oracle
        self.rulings = rulings
        self.sets = [card_set]
        self.legalities = legalities
        self.edhrec_rank = edhrec_rank
        self.image_url = image_url
        self.image_age = image_age

    def __repr__(self):
        return '{"cardName":"' + self.card_name + '","manaCost":"' + self.mana_cost + '","typeLine":"' + self.type_line +\
               '","oracleText":' + dumps(self.oracle) + ',"rulings":' + dumps(self.rulings) + ',"sets":' + str(self.sets) +\
               ',"legalities":' + dumps(self.legalities) + ',"edhrec_rank":' + str(self.edhrec_rank) +\
               ',"image_url":' + dumps(self.image_url) + '}'

    def add_set(self, card_set):
        self.sets.append(card_set)


class SetType(Enum):
    NORMAL_SET = 1
    FOIL_ONLY_SET = 2
    MTGO_SET = 3


class CardSet:
    def __init__(self, setUrl, card_set_type, set_name):
        self.setUrl = setUrl
        self.card_set_type = card_set_type
        self.set_name = set_name

    def __repr__(self):
        return '{"setName":"' + self.set_name + '","cardSetType":"' + self.card_set_type.name + '","setUrl":"' + self.setUrl + '"}'
