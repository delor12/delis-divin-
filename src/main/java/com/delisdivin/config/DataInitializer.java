package com.delisdivin.config;

import com.delisdivin.entity.*;
import com.delisdivin.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CityRepository cityRepository;
    private final RestaurantRepository restaurantRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AppUserRepository userRepository;
    private final ProductCategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final DiningTableRepository tableRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Database is already seeded. Skipping initialization.");
            return;
        }

        log.info("Starting database seeding for Delis Divin SaaS...");

        // 1. Seed Cities
        City yaounde = new City();
        yaounde.setName("Yaoundé");
        yaounde.setPostalCode("00237");
        yaounde.setCountry("Cameroun");
        yaounde.setActive(true);
        cityRepository.save(yaounde);

        City douala = new City();
        douala.setName("Douala");
        douala.setPostalCode("00237");
        douala.setCountry("Cameroun");
        douala.setActive(true);
        cityRepository.save(douala);

        City garoua = new City();
        garoua.setName("Garoua");
        garoua.setPostalCode("00237");
        garoua.setCountry("Cameroun");
        garoua.setActive(true);
        cityRepository.save(garoua);

        City bamenda = new City();
        bamenda.setName("Bamenda");
        bamenda.setPostalCode("00237");
        bamenda.setCountry("Cameroun");
        bamenda.setActive(true);
        cityRepository.save(bamenda);

        City bafoussam = new City();
        bafoussam.setName("Bafoussam");
        bafoussam.setPostalCode("00237");
        bafoussam.setCountry("Cameroun");
        bafoussam.setActive(true);
        cityRepository.save(bafoussam);

        City dakar = new City();
        dakar.setName("Dakar");
        dakar.setPostalCode("10000");
        dakar.setCountry("Sénégal");
        dakar.setActive(true);
        cityRepository.save(dakar);

        City abidjan = new City();
        abidjan.setName("Abidjan");
        abidjan.setPostalCode("00225");
        abidjan.setCountry("Côte d'Ivoire");
        abidjan.setActive(true);
        cityRepository.save(abidjan);

        // 2. Seed Restaurant
        Restaurant bistrot = new Restaurant();
        bistrot.setName("Le Bistrot Divin");
        bistrot.setDescription("Gastronomie camerounaise et européenne raffinée au cœur de Yaoundé.");
        bistrot.setAddress("Avenue John Kennedy, Yaoundé");
        bistrot.setCity(yaounde);
        bistrot.setPhone("+237 222 45 45 45");
        bistrot.setEmail("bistrot@delisdivin.com");
        bistrot.setRating(4.8);
        bistrot.setPriceRange("Medium");
        bistrot.setAveragePrepTime(20);
        bistrot.setLogoUrl("/images/bistrot_logo.png");
        bistrot.setBannerUrl("https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?q=80&w=1470");
        bistrot.setLatitude(3.8480);
        bistrot.setLongitude(11.5021);
        bistrot.setActive(true);
        restaurantRepository.save(bistrot);

        Restaurant epieurien = new Restaurant();
        epieurien.setName("Divine Mère");
        epieurien.setDescription("Spécialités de rôtis maison, émincés succulents et cuisine traditionnelle à Douala.");
        epieurien.setAddress("Rue Joss, Douala");
        epieurien.setCity(douala);
        epieurien.setPhone("+237 233 45 78 88");
        epieurien.setEmail("divinemere@delisdivin.com");
        epieurien.setRating(4.8);
        epieurien.setPriceRange("Medium");
        epieurien.setAveragePrepTime(25);
        epieurien.setLogoUrl("/images/menu DM.jpg");
        epieurien.setBannerUrl("/images/carte de fideliter DM.jpg");
        epieurien.setLatitude(4.0511);
        epieurien.setLongitude(9.7679);
        epieurien.setActive(true);
        restaurantRepository.save(epieurien);

        // 3. Seed Subscription
        Subscription sub = new Subscription();
        sub.setRestaurant(bistrot);
        sub.setPlanName("Premium SaaS");
        sub.setPrice(150000.0); // 150,000 FCFA
        sub.setStartDate(LocalDate.now());
        sub.setEndDate(LocalDate.now().plusYears(1));
        sub.setStatus("ACTIVE");
        subscriptionRepository.save(sub);

        // 4. Seed Users
        String defaultPassword = passwordEncoder.encode("password");

        // Super Admin
        AppUser superAdmin = new AppUser();
        superAdmin.setUsername("superadmin");
        superAdmin.setEmail("superadmin@delisdivin.com");
        superAdmin.setPassword(defaultPassword);
        superAdmin.setFirstName("Amadou");
        superAdmin.setLastName("Diop");
        superAdmin.setPhone("+221 77 123 45 67");
        superAdmin.setRole(Role.SUPER_ADMIN);
        superAdmin.setActive(true);
        userRepository.save(superAdmin);

        // Restaurant Admin
        AppUser rAdmin = new AppUser();
        rAdmin.setUsername("bistrot_admin");
        rAdmin.setEmail("admin@bistrotdivin.com");
        rAdmin.setPassword(defaultPassword);
        rAdmin.setFirstName("Fatou");
        rAdmin.setLastName("Ndiaye");
        rAdmin.setPhone("+221 77 987 65 43");
        rAdmin.setRole(Role.RESTAURANT_ADMIN);
        rAdmin.setRestaurant(bistrot);
        rAdmin.setActive(true);
        userRepository.save(rAdmin);

        // Server
        AppUser server = new AppUser();
        server.setUsername("bistrot_server");
        server.setEmail("server@bistrotdivin.com");
        server.setPassword(defaultPassword);
        server.setFirstName("Moussa");
        server.setLastName("Sow");
        server.setPhone("+221 77 654 32 10");
        server.setRole(Role.SERVER);
        server.setRestaurant(bistrot);
        server.setActive(true);
        userRepository.save(server);

        // Chef
        AppUser chef = new AppUser();
        chef.setUsername("bistrot_chef");
        chef.setEmail("chef@bistrotdivin.com");
        chef.setPassword(defaultPassword);
        chef.setFirstName("Cheikh");
        chef.setLastName("Gaye");
        chef.setPhone("+221 77 345 67 89");
        chef.setRole(Role.CHEF);
        chef.setRestaurant(bistrot);
        chef.setActive(true);
        userRepository.save(chef);

        // Cashier
        AppUser cashier = new AppUser();
        cashier.setUsername("bistrot_cashier");
        cashier.setEmail("cashier@bistrotdivin.com");
        cashier.setPassword(defaultPassword);
        cashier.setFirstName("Awa");
        cashier.setLastName("Faye");
        cashier.setPhone("+221 77 456 78 90");
        cashier.setRole(Role.CASHIER);
        cashier.setRestaurant(bistrot);
        cashier.setActive(true);
        userRepository.save(cashier);

        // Delivery
        AppUser delivery = new AppUser();
        delivery.setUsername("deliverer_1");
        delivery.setEmail("delivery1@delisdivin.com");
        delivery.setPassword(defaultPassword);
        delivery.setFirstName("Modou");
        delivery.setLastName("Fall");
        delivery.setPhone("+221 77 888 88 88");
        delivery.setRole(Role.DELIVERY);
        delivery.setActive(true);
        userRepository.save(delivery);

        // 5. Seed Product Categories
        ProductCategory ent = new ProductCategory();
        ent.setName("Entrées");
        ent.setDescription("Mises en bouche légères et salades.");
        ent.setRestaurant(bistrot);
        ent.setDisplayOrder(1);
        categoryRepository.save(ent);

        ProductCategory plat = new ProductCategory();
        plat.setName("Plats de Résistance");
        plat.setDescription("Thiéboudienne, Yassa, Mafé et autres spécialités.");
        plat.setRestaurant(bistrot);
        plat.setDisplayOrder(2);
        categoryRepository.save(plat);

        ProductCategory drink = new ProductCategory();
        drink.setName("Boissons");
        drink.setDescription("Boissons fraîches locales et internationales.");
        drink.setRestaurant(bistrot);
        drink.setDisplayOrder(3);
        categoryRepository.save(drink);

        ProductCategory dessert = new ProductCategory();
        dessert.setName("Desserts");
        dessert.setDescription("Douceurs sucrées de fin de repas.");
        dessert.setRestaurant(bistrot);
        dessert.setDisplayOrder(4);
        categoryRepository.save(dessert);

        // 6. Seed Products
        // Entrées
        Product pastels = new Product();
        pastels.setName("Pastels de Poisson");
        pastels.setDescription("Beignets frits farcis au poisson épicé, servis avec sauce tomate piquante. (6 pièces)");
        pastels.setPrice(2000.0); // 2000 FCFA
        pastels.setCategory(ent);
        pastels.setRestaurant(bistrot);
        pastels.setStockQuantity(50);
        productRepository.save(pastels);

        Product salade = new Product();
        salade.setName("Salade César");
        salade.setDescription("Salade romaine croquante, filets de poulet grillés, croûtons et sauce César maison.");
        salade.setPrice(3500.0);
        salade.setCategory(ent);
        salade.setRestaurant(bistrot);
        salade.setStockQuantity(30);
        productRepository.save(salade);

        // Plats
        Product thieb = new Product();
        thieb.setName("Thiéboudienne Royal");
        thieb.setDescription("Riz rouge cuit dans le bouillon de poisson frais avec légumes tropicaux (manioc, chou, carotte). Le plat national sénégalais.");
        thieb.setPrice(5000.0);
        thieb.setCategory(plat);
        thieb.setRestaurant(bistrot);
        thieb.setStockQuantity(40);
        productRepository.save(thieb);

        Product yassa = new Product();
        yassa.setName("Poulet Yassa");
        yassa.setDescription("Poulet mariné au citron et à la moutarde, braisé et mijoté avec une fondue d'oignons caramélisés, servi avec du riz blanc.");
        yassa.setPrice(4500.0);
        yassa.setCategory(plat);
        yassa.setRestaurant(bistrot);
        yassa.setStockQuantity(35);
        productRepository.save(yassa);

        // Boissons
        Product bissap = new Product();
        bissap.setName("Jus de Bissap");
        bissap.setDescription("Infusion de fleurs d'hibiscus rouge, parfumée à la menthe fraîche et à la fleur d'oranger.");
        bissap.setPrice(1000.0);
        bissap.setCategory(drink);
        bissap.setRestaurant(bistrot);
        bissap.setBeverage(true);
        bissap.setStockQuantity(100);
        productRepository.save(bissap);

        Product bouye = new Product();
        bouye.setName("Jus de Bouye");
        bouye.setDescription("Boisson onctueuse à base de pulpe de fruit de baobab et lait concentré.");
        bouye.setPrice(1000.0);
        bouye.setCategory(drink);
        bouye.setRestaurant(bistrot);
        bouye.setBeverage(true);
        bouye.setStockQuantity(80);
        productRepository.save(bouye);

        // Desserts
        Product thiakry = new Product();
        thiakry.setName("Thiakry Divin");
        thiakry.setDescription("Mélange traditionnel de semoule de mil et de yaourt sucré parfumé à la noix de muscade et raisins secs.");
        thiakry.setPrice(1500.0);
        thiakry.setCategory(dessert);
        thiakry.setRestaurant(bistrot);
        thiakry.setDessert(true);
        thiakry.setStockQuantity(25);
        productRepository.save(thiakry);

        // 7. Seed Dining Tables
        for (int i = 1; i <= 6; i++) {
            DiningTable table = new DiningTable();
            table.setRestaurant(bistrot);
            table.setNumber(i);
            table.setCapacity(i <= 3 ? 2 : 4);
            table.setStatus(TableStatus.FREE);
            table.setXCoordinate(20.0 + (i * 12.0));
            table.setYCoordinate(40.0);
            tableRepository.save(table);
        }

        // Seeding for Divine Mère (Douala)
        // Categories
        ProductCategory rotisCat = new ProductCategory();
        rotisCat.setName("Nos Rôtis");
        rotisCat.setDescription("Délicieux rôtis préparés avec soin.");
        rotisCat.setRestaurant(epieurien);
        rotisCat.setDisplayOrder(1);
        categoryRepository.save(rotisCat);

        ProductCategory platsCat = new ProductCategory();
        platsCat.setName("Émincés & Plats");
        platsCat.setDescription("Spécialités de la maison.");
        platsCat.setRestaurant(epieurien);
        platsCat.setDisplayOrder(2);
        categoryRepository.save(platsCat);

        ProductCategory fruitsCat = new ProductCategory();
        fruitsCat.setName("Desserts & Fruits");
        fruitsCat.setDescription("Douceurs fruitées et desserts.");
        fruitsCat.setRestaurant(epieurien);
        fruitsCat.setDisplayOrder(3);
        categoryRepository.save(fruitsCat);

        // Products
        Product rotiPorc = new Product();
        rotiPorc.setName("Rôti de Porc Divine Mère");
        rotiPorc.setDescription("Délicieux rôti de porc préparé selon notre recette traditionnelle.");
        rotiPorc.setPrice(4500.0);
        rotiPorc.setCategory(rotisCat);
        rotiPorc.setRestaurant(epieurien);
        rotiPorc.setStockQuantity(20);
        rotiPorc.setImageUrl("/images/rotis de port divine mere .jpg");
        productRepository.save(rotiPorc);

        Product rotiPoulet = new Product();
        rotiPoulet.setName("Rôti de Poulet DM");
        rotiPoulet.setDescription("Poulet rôti doré et juteux aux épices locales.");
        rotiPoulet.setPrice(4000.0);
        rotiPoulet.setCategory(rotisCat);
        rotiPoulet.setRestaurant(epieurien);
        rotiPoulet.setStockQuantity(25);
        rotiPoulet.setImageUrl("/images/rotis de poulet DM.jpg");
        productRepository.save(rotiPoulet);

        Product eminceBoeuf = new Product();
        eminceBoeuf.setName("Émincé de Bœuf DM");
        eminceBoeuf.setDescription("Émincé de bœuf tendre sauté aux légumes de saison.");
        eminceBoeuf.setPrice(3500.0);
        eminceBoeuf.setCategory(platsCat);
        eminceBoeuf.setRestaurant(epieurien);
        eminceBoeuf.setStockQuantity(30);
        eminceBoeuf.setImageUrl("/images/emincer DM.jpg");
        productRepository.save(eminceBoeuf);

        Product assietteFruits = new Product();
        assietteFruits.setName("Assiette de Fruits DM");
        assietteFruits.setDescription("Assortiment frais de fruits tropicaux coupés.");
        assietteFruits.setPrice(1500.0);
        assietteFruits.setCategory(fruitsCat);
        assietteFruits.setRestaurant(epieurien);
        assietteFruits.setStockQuantity(15);
        assietteFruits.setImageUrl("/images/fruit DM.jpg");
        productRepository.save(assietteFruits);

        // Dining Tables for Divine Mère
        for (int i = 1; i <= 4; i++) {
            DiningTable table = new DiningTable();
            table.setRestaurant(epieurien);
            table.setNumber(i);
            table.setCapacity(2);
            table.setStatus(TableStatus.FREE);
            table.setXCoordinate(20.0 + (i * 12.0));
            table.setYCoordinate(40.0);
            tableRepository.save(table);
        }

        log.info("Database seeding successfully completed.");
    }
}
