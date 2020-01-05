package ssstatistics.com;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Ssstatistics_bot extends TelegramLongPollingBot {

    public void onUpdateReceived(Update update) {
        Sending sending = new Sending();
        if (update.hasMessage() && update.getMessage().hasText()) {
            String s = update.getMessage().getText();
            if (s.equals("/start")){
                sending.sendingBootomNavs(update);
                UsersManager.getInstance().issetUser(update);
            }else{
                if(s.contains("https://market-trends.com.ua")){
                    String ans = parser(s);
                    sending.sendMsg(update, "ans");
                }
            }
        }if (update.hasCallbackQuery()){
        }
    }
    public String parser(String my_url) throws IOException {

        Map<String, Object> productInfo = new HashMap<>();
        Map<String, Object> mainImg = new HashMap<>();


        Document doc = Jsoup.connect(my_url).get();
        String name = doc.select("span[data-qaid='product_name']").text();
        String price = doc.select("div.b-product-cost p.b-product-cost__price span[data-qaid='product_price']").text();
        String description = doc.select("div.b-layout__content div[data-qaid='product_description']").html();
        String SKU = doc.select("div.b-layout__content span[data-qaid='product_code']").text();
        String big_description = "<table>" + doc.select("div.b-layout__content table.b-product-info").html() + "</table>";
        String main_img_url = doc.select("div[data-qaid='product_img_block'] div.b-product-view__zoom-box a.b-product-view__image-link").first().attr("href");
        String main_img_alt = doc.select("div[data-qaid='product_img_block'] div.b-product-view__zoom-box a.b-product-view__image-link img").first().attr("alt");


        List<String> listImg = new ArrayList<>();
        Elements wrapBlocks = doc.select("div.b-extra-photos__holder div#product-extra-photos-container");
        for (Element link: wrapBlocks.select("a.b-extra-photos__item")) {
            String img = "<img src=\"" + link.attr("href") + "\" alt=\"" + link.attr("title") + "\">";
            listImg.add(img);
        }
        if (listImg.size() < 1){
            wrapBlocks = doc.select("div.b-product-view div.b-extra-photos");
            for (Element link: wrapBlocks.select("a.b-extra-photos__item")) {
                String img = "<img src=\"" + link.attr("href") + "\" alt=\"" + link.attr("title") + "\">";
                listImg.add(img);
            }
        }
        String imagesHTML = listImg.toString();




        Element cat_wrap = doc.select("div.b-layout__content table.b-product-info tbody").first();
        String cats = "";
        for (Element td: cat_wrap.select("tr td.b-product-info__cell")) {
            if (cats.equals("next")){
                cats = td.text().toLowerCase();
                break;
            }
            else if (td.text().contains("Категория")){
                cats = "next";
            }
            else if (td.text().contains("Пол")){
                cats = "next";
            }
        }
        String categories[] = cats.split(",");

        List<Map<String, Object>> list_cats = new ArrayList<>();

        for (String cat : categories){
            int id_cat = 0;
            cat = cat.trim();
            Map<String, Object> newCat = new HashMap<>();
            switch (cat){
                case"для женщин": {id_cat = 17; break;}
                case"унисекс": {id_cat = 55; break;}
                case"мужской": {id_cat = 16; break;}
                case"для мужчин": {id_cat = 16; break;}
            }
            if (id_cat != 0){
                newCat.put("id", id_cat);
                list_cats.add(newCat);
            }
        }
//        System.out.println(name);
//        System.out.println(price);
//        System.out.println(description);
//        System.out.println(big_description);
//        System.out.println(main_img_url);
//        System.out.println(main_img_alt);
//        System.out.println(imagesHTML);
//        System.out.println(SKU);


        mainImg.put("src", main_img_url);
        mainImg.put("alt", main_img_alt);

        List<Map<String, Object>> list_imgs = new ArrayList<>();
        list_imgs.add(mainImg);
        productInfo.put("name", name);
        productInfo.put("type", "simple");
        productInfo.put("regular_price", price);
        productInfo.put("short_description", description + "GALLERY"  + imagesHTML);
        productInfo.put("description", big_description);
        productInfo.put("sku", SKU);
        productInfo.put("images", list_imgs);
        if (list_cats.size() > 0){
            productInfo.put("categories", list_cats);
        }
        OAuthConfig config = new OAuthConfig("https://www.mayers.com.ua", "ck_03532d9c7102299c2239665e35b62382f3c6cc35", "cs_c2dd2958e32ea9841d823ea63696a46ea936c452");
        WooCommerce wooCommerce = new WooCommerceAPI(config, ApiVersionType.V3);
        Map product = wooCommerce.create(EndpointBaseType.PRODUCTS.getValue(), productInfo);

        return product.get("permalink").toString();
    }
    public String getBotUsername() {
        return "ssstatistics_bot";
    }
    public String getBotToken() {
        return "733589645:AAH9RWGXCFhQrYfEEYd-sccOrQ2uwdNBWYQ";
    }
}
