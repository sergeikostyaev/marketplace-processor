package bot.telegram.service.impl;

import bot.telegram.service.RegionService;
import org.springframework.stereotype.Service;

@Service
public class RegionServiceImpl implements RegionService {
    @Override
    public String getRegion(String region) {
        if (region.toLowerCase().contains("москва") || region.toLowerCase().contains("московская")) {
            return "Москва и Московская область";
        } else if (region.toLowerCase().contains("мурманск") || region.toLowerCase().contains("мурманская")) {
            return "Мурманская область";
        } else if (region.toLowerCase().contains("свердловск")) {
            return "Свердловская область";
        } else if (region.toLowerCase().contains("минск") || region.toLowerCase().contains("минская")) {
            return "Минск и Минская область";
        } else if (region.toLowerCase().contains("томск") || region.toLowerCase().contains("томская")) {
            return "Томская область";
        } else if (region.toLowerCase().contains("гомель") || region.toLowerCase().contains("гомельская")) {
            return "Гомель и Гомельская область";
        } else if (region.toLowerCase().contains("благовещенск") || region.toLowerCase().contains("амурская")) {
            return "Амурская область";
        } else if (region.toLowerCase().contains("архангельск") || region.toLowerCase().contains("архангельская")) {
            return "Архангельская область";
        } else if (region.toLowerCase().contains("астрахан")) {
            return "Астраханская область";
        } else if (region.toLowerCase().contains("белгород")) {
            return "Белгородская область";
        } else if (region.toLowerCase().contains("брянск")) {
            return "Брянская область";
        } else if (region.toLowerCase().contains("владимир")) {
            return "Владимирская область";
        } else if (region.toLowerCase().contains("волгоград")) {
            return "Волгоградская область";
        } else if (region.toLowerCase().contains("вологда") || region.toLowerCase().contains("вологодская")) {
            return "Вологодская область";
        } else if (region.toLowerCase().contains("воронежс") || region.toLowerCase().contains("воронеж")) {
            return "Воронежская область";
        } else if (region.toLowerCase().contains("петербург") || region.toLowerCase().contains("ленинградская")) {
            return "Санкт-Петербург и Ленинградская область";
        } else if (region.toLowerCase().contains("иркутск")) {
            return "Иркутская область";
        } else if (region.toLowerCase().contains("калининград")) {
            return "Калининградская область";
        } else if (region.toLowerCase().contains("калуга") || region.toLowerCase().contains("калужск")) {
            return "Калужская область";
        } else if (region.toLowerCase().contains("кемеров")) {
            return "Кемеровская область";
        } else if (region.toLowerCase().contains("киров")) {
            return "Кировская область";
        } else if (region.toLowerCase().contains("костром")) {
            return "Костромская область";
        } else if (region.toLowerCase().contains("курган")) {
            return "Курганская область";
        } else if (region.toLowerCase().contains("курск")) {
            return "Курская область";
        } else if (region.toLowerCase().contains("липецк")) {
            return "Липецкая область";
        } else if (region.toLowerCase().contains("магадан")) {
            return "Магаданская область";
        } else if (region.toLowerCase().contains("нижегородск") || region.toLowerCase().contains("нижний новгород")) { //
            return "Нижегородская область";
        } else if (region.toLowerCase().contains("новгород")) {
            return "Новгородская область";
        } else if (region.toLowerCase().contains("новосибирск")) {
            return "Новосибирская область";
        } else if (region.toLowerCase().contains("омск")) {
            return "Омская область";
        } else if (region.toLowerCase().contains("оренбург")) {
            return "Оренбургская область";
        } else if (region.toLowerCase().contains("орел") || region.toLowerCase().contains("орловск")) {
            return "Орловская область";
        } else if (region.toLowerCase().contains("пенз")) {
            return "Пензенская область";
        } else if (region.toLowerCase().contains("псков")) {
            return "Псковская область";
        } else if (region.toLowerCase().contains("ростов")) {
            return "Ростовская область";
        } else if (region.toLowerCase().contains("рязан")) {
            return "Рязанская область";
        } else if (region.toLowerCase().contains("самара") || region.toLowerCase().contains("самарск")) {
            return "Самарская область";
        } else if (region.toLowerCase().contains("саратов")) {
            return "Саратовская область";
        } else if (region.toLowerCase().contains("сахалинск") || region.toLowerCase().contains("южно-сахалинск")) {
            return "Сахалинская область";
        } else if (region.toLowerCase().contains("смоленск")) {
            return "Смоленская область";
        } else if (region.toLowerCase().contains("тамбов")) {
            return "Тамбовская область";
        } else if (region.toLowerCase().contains("тверь") || region.toLowerCase().contains("тверская") || region.toLowerCase().contains("тверской")) {
            return "Тверская область";
        } else if (region.toLowerCase().contains("томск")) {
            return "Томская область";
        } else if (region.toLowerCase().contains("тульская") || region.toLowerCase().contains("тула")) {
            return "Тульская область";
        } else if (region.toLowerCase().contains("тюмен")) {
            return "Тюменская область";
        } else if (region.toLowerCase().contains("ульяновск")) {
            return "Ульяновская область";
        } else if (region.toLowerCase().contains("челябинск")) {
            return "Челябинская область";
        } else if (region.toLowerCase().contains("ярославск") || region.toLowerCase().contains("ярославль")) {
            return "Ярославская область";
        } else if (region.toLowerCase().contains("башкортостан")) {
            return "Республика Башкортостан";
        } else if (region.toLowerCase().contains("бурятия")) {
            return "Республика Бурятия";
        } else if (region.toLowerCase().contains("дагестан")) {
            return "Республика Дагестан";
        } else if (region.toLowerCase().contains("адыгея")) {
            return "Республика Адыгея";
        } else if (region.toLowerCase().contains("алтай")) {
            return "Республика Алтай и Алтайский край";
        } else if (region.toLowerCase().contains("донецк")) {
            return "Донецкая Народная Республика";
        } else if (region.toLowerCase().contains("севастополь") || region.toLowerCase().contains("крым")) {
            return "Севастополь и Республика Крым";
        } else if (region.toLowerCase().contains("ингушетия")) {
            return "Республика Ингушетия";
        } else if (region.toLowerCase().contains("кабардино-балкарская") || region.toLowerCase().contains("кабардино-балкария")) {
            return "Кабардино-Балкарская Республика";
        } else if (region.toLowerCase().contains("калмыкия")) {
            return "Республика Калмыкия";
        } else if (region.toLowerCase().contains("карачаево-черкес")) {
            return "Карачаево-Черкесская Республика";
        } else if (region.toLowerCase().contains("карелия")) {
            return "Республика Карелия";
        } else if (region.toLowerCase().contains("коми")) {
            return "Республика Коми";
        } else if (region.toLowerCase().contains("луганск")) {
            return "Луганская Народная Республика";
        } else if (region.toLowerCase().contains("марий эл")) {
            return "Республика Марий Эл";
        } else if (region.toLowerCase().contains("мордовия")) {
            return "Республика Мордовия";
        } else if (region.toLowerCase().contains("якутия") || region.toLowerCase().contains("саха")) {
            return "Республика Саха (Якутия)";
        } else if (region.toLowerCase().contains("северная осетия") || region.toLowerCase().contains("алания")) {
            return "Республика Северная Осетия — Алания";
        } else if (region.toLowerCase().contains("татарстан")) {
            return "Республика Татарстан";
        } else if (region.toLowerCase().contains("тыва")) {
            return "Республика Тыва";
        } else if (region.toLowerCase().contains("удмуртская") || region.toLowerCase().contains("удмуртия")) {
            return "Удмуртская Республика";
        } else if (region.toLowerCase().contains("хакасия")) {
            return "Республика Хакасия";
        } else if (region.toLowerCase().contains("чеченская")) {
            return "Чеченская Республика";
        } else if (region.toLowerCase().contains("чувашия") || region.toLowerCase().contains("чувашская")) {
            return "Чувашская Республика (Чувашия)";
        } else if (region.toLowerCase().contains("забайкальский")) {
            return "Забайкальский край";
        } else if (region.toLowerCase().contains("камчатский") || region.toLowerCase().contains("камчатка")) {
            return "Камчатский край";
        } else if (region.toLowerCase().contains("краснодарский")) {
            return "Краснодарский край";
        } else if (region.toLowerCase().contains("красноярский")) {
            return "Красноярский край";
        } else if (region.toLowerCase().contains("пермский") || region.toLowerCase().contains("пермь")) {
            return "Пермский край";
        } else if (region.toLowerCase().contains("приморск")) {
            return "Приморский край";
        } else if (region.toLowerCase().contains("ставрополь")) {
            return "Ставропольский край";
        } else if (region.toLowerCase().contains("хабаровск")) {
            return "Хабаровский край";
        } else if (region.toLowerCase().contains("запорожская") || region.toLowerCase().contains("запорожье")) {
            return "Запорожская область";
        } else if (region.toLowerCase().contains("херсон")) {
            return "Херсонская область";
        } else if (region.toLowerCase().contains("еврейск")) {
            return "Еврейская автономная область";
        } else if (region.toLowerCase().contains("ямало-ненецк")) {
            return "Ямало-Ненецкий автономный округ";
        } else if (region.toLowerCase().contains("ханты-мансийск") || region.toLowerCase().contains("югра")) {
            return "Ханты-Мансийский округ";
        } else if (region.toLowerCase().contains("чукотск")) {
            return "Чукотский автономный округ";
        } else if (region.toLowerCase().contains("ненецк")) {
            return "Ненецкий автономный округ";
        }

        return region;
    }
}
