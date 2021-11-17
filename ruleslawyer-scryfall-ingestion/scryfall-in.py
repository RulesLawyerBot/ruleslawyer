import json
import requests
from simple_io import write
from simple_io import clear
from contract.cards import Card
from contract.cards import SetType
from contract.cards import CardSet
import datetime

FORMATS = ["standard", "brawl", "historic", "pioneer", "modern", "legacy", "vintage", "commander", "pauper"]
all_cards = {}

SCRYFALL_ENDPOINT = "https://api.scryfall.com/cards/search?q=*&unique=prints&page="


def get_set_type(card_json):
    card_prices = card_json["prices"]
    if card_prices["usd"] or card_prices["eur"]:
        return SetType.NORMAL_SET
    if card_prices["usd_foil"] or card_prices["eur_foil"]:
        return SetType.FOIL_ONLY_SET
    return SetType.MTGO_SET


def parse_card_oracle(card_json):
    if "oracle_text" in card_json:
        oracle = card_json["oracle_text"]
    else:
        oracle = parse_card_oracle(card_json["card_faces"][0]) + "\n//\n" + parse_card_oracle(
            card_json["card_faces"][1])
    if "power" in card_json:
        oracle = oracle + "\n" + str(card_json["power"]) + "/" + str(card_json["toughness"])
    if "loyalty" in card_json:
        oracle = oracle + "\nStarting Loyalty: " + str(card_json["loyalty"])
    return oracle


def get_image_urls(card_json):
    if "image_uris" in card_json:
        return [card_json["image_uris"]["large"] if "large" in card_json["image_uris"] else card_json["image_uris"][0]]
    return [get_image_urls(v)[0] for v in card_json["card_faces"]]


def parse_card(card_json):
    card_name = card_json["name"].replace('"', "'")
    scryfall_uri = card_json["uri"]
    card_set = CardSet(scryfall_uri, get_set_type(card_json), card_json["set_name"])
    oracle_id = card_json["oracle_id"]
    if all_cards.get(oracle_id):
        all_cards[oracle_id].add_set(card_set)
        image_age = datetime.datetime.strptime(card_json["released_at"], "%Y-%m-%d")
        image_url = get_image_urls(card_json)
        if all_cards[oracle_id].image_age < image_age:
            all_cards[oracle_id].image_url = image_url
    else:
        mana_cost = card_json["mana_cost"] if "mana_cost" in card_json else card_json["card_faces"][0]["mana_cost"]
        type_line = card_json["type_line"].replace("—", "-")
        oracle = parse_card_oracle(card_json).replace('"', "'")
        legalities = {}
        for k in card_json["legalities"]:
            if k in FORMATS:
                legalities[k] = card_json["legalities"][k]
        edhrec_rank = card_json["edhrec_rank"] if "edhrec_rank" in card_json else 9999999
        image_url = get_image_urls(card_json)
        image_age = datetime.datetime.strptime(card_json["released_at"], "%Y-%m-%d")
        card = Card(card_name, mana_cost, type_line, oracle, [], card_set, legalities, edhrec_rank, image_url, image_age)
        all_cards[oracle_id] = card
        print(card)


def read_file_chunk(file_object):
    while True:
        data = file_object.read(1048576)
        if not data:
            break
        yield data


def main():
    skipped_cards = []
    page_number = 1
    while True:
        page = requests.get(SCRYFALL_ENDPOINT + str(page_number)).json()
        for (count, card_json) in enumerate(page["data"]):
            if not card_json["lang"] == "en" or card_json["set_type"] in ["token", "memorabilia"]:
                continue
            if not card_json["prices"]["usd"] and not card_json["prices"]["usd_foil"]:  # means its not a paper product
                skipped_cards.append(card_json)
                continue
            parse_card(card_json)
        if not page["has_more"]:
            break
        page_number = page_number + 1
        print(page_number)

    for card_json in skipped_cards:
        scryfall_uri = card_json["uri"]
        card_set = CardSet(scryfall_uri, get_set_type(card_json), card_json["set_name"])
        oracle_id = card_json["oracle_id"]
        if all_cards.get(oracle_id):
            all_cards[oracle_id].add_set(card_set)

    rulings_file = open("rulings.json", "r", encoding="utf-8")
    rulings_raw = rulings_file.read()
    rulings_data = json.loads(rulings_raw)

    clear("rulingless-cards.json")
    write("rulingless-cards.json", [all_cards[k] for k in all_cards])

    for (count, rulings_json) in enumerate(rulings_data):
        if not all_cards.get(rulings_json["oracle_id"]):
            continue
        ruling = rulings_json["comment"]
        card = all_cards[rulings_json["oracle_id"]]
        card.rulings.append(ruling)
        try:
            print(ruling)
        except:
            pass

    output = [all_cards[k] for k in all_cards]

    clear("cards.json")
    write("cards.json", output)


if __name__ == "__main__":
    main()
