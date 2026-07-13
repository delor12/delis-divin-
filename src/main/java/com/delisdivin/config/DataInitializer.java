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

        // Gérant (Admin) - Divine Mère
        AppUser gerantDM = new AppUser();
        gerantDM.setUsername("gerant_divinemere");
        gerantDM.setEmail("gerant@divinemere.com");
        gerantDM.setPassword(defaultPassword);
        gerantDM.setFirstName("Fatou");
        gerantDM.setLastName("Ndiaye");
        gerantDM.setPhone("+221 77 987 65 43");
        gerantDM.setRole(Role.RESTAURANT_ADMIN);
        gerantDM.setRestaurant(epieurien);
        gerantDM.setActive(true);
        userRepository.save(gerantDM);

        // Serveur (Waiter) - Divine Mère
        AppUser serveurDM = new AppUser();
        serveurDM.setUsername("serveur_divinemere");
        serveurDM.setEmail("serveur@divinemere.com");
        serveurDM.setPassword(defaultPassword);
        serveurDM.setFirstName("Moussa");
        serveurDM.setLastName("Sow");
        serveurDM.setPhone("+221 77 654 32 10");
        serveurDM.setRole(Role.SERVER);
        serveurDM.setRestaurant(epieurien);
        serveurDM.setActive(true);
        userRepository.save(serveurDM);
        // Chef - Divine Mère
        AppUser chefDM = new AppUser();
        chefDM.setUsername("cusiniere_divinemere");
        chefDM.setEmail("cusiniere@divinemere.com");
        chefDM.setPassword(defaultPassword);
        chefDM.setFirstName("Awa");
        chefDM.setLastName("Diagne");
        chefDM.setPhone("+221 77 111 22 33");
        chefDM.setRole(Role.CHEF);
        chefDM.setRestaurant(epieurien);
        chefDM.setActive(true);
        userRepository.save(chefDM);

        // Caissier - Divine Mère
        AppUser caissierDM = new AppUser();
        caissierDM.setUsername("caissier_divinemere");
        caissierDM.setEmail("caissier@divinemere.com");
        caissierDM.setPassword(defaultPassword);
        caissierDM.setFirstName("Ousmane");
        caissierDM.setLastName("Sy");
        caissierDM.setPhone("+221 77 444 55 66");
        caissierDM.setRole(Role.CASHIER);
        caissierDM.setRestaurant(epieurien);
        caissierDM.setActive(true);
        userRepository.save(caissierDM);

        // Livreur - Divine Mère
        AppUser livreurDM = new AppUser();
        livreurDM.setUsername("livreur_divinemere");
        livreurDM.setEmail("livreur@divinemere.com");
        livreurDM.setPassword(defaultPassword);
        livreurDM.setFirstName("Cheikh");
        livreurDM.setLastName("Diallo");
        livreurDM.setPhone("+221 77 777 88 99");
        livreurDM.setRole(Role.DELIVERY);
        livreurDM.setRestaurant(epieurien);
        livreurDM.setActive(true);
        livreurDM.setLatitude(4.0520);
        livreurDM.setLongitude(9.7685);
        userRepository.save(livreurDM);

        // Deuxième Livreur - Divine Mère (plus éloigné pour les tests de proximité)
        AppUser livreur2DM = new AppUser();
        livreur2DM.setUsername("livreur2_divinemere");
        livreur2DM.setEmail("livreur2@divinemere.com");
        livreur2DM.setPassword(defaultPassword);
        livreur2DM.setFirstName("Mamadou");
        livreur2DM.setLastName("Sow");
        livreur2DM.setPhone("+221 77 222 33 44");
        livreur2DM.setRole(Role.DELIVERY);
        livreur2DM.setRestaurant(epieurien);
        livreur2DM.setActive(true);
        livreur2DM.setLatitude(4.0650);
        livreur2DM.setLongitude(9.7750);
        userRepository.save(livreur2DM);

        ProductCategory alimentsBistrot = new ProductCategory();
        alimentsBistrot.setName("Aliments");
        alimentsBistrot.setDescription("Plats et repas principaux.");
        alimentsBistrot.setRestaurant(bistrot);
        alimentsBistrot.setDisplayOrder(1);
        alimentsBistrot.setActive(true);
        categoryRepository.save(alimentsBistrot);

        ProductCategory boissonsBistrot = new ProductCategory();
        boissonsBistrot.setName("Boissons");
        boissonsBistrot.setDescription("Boissons chaudes et froides.");
        boissonsBistrot.setRestaurant(bistrot);
        boissonsBistrot.setDisplayOrder(2);
        boissonsBistrot.setActive(true);
        categoryRepository.save(boissonsBistrot);

        ProductCategory patisseriesBistrot = new ProductCategory();
        patisseriesBistrot.setName("Pâtisseries");
        patisseriesBistrot.setDescription("Gâteaux et viennoiseries.");
        patisseriesBistrot.setRestaurant(bistrot);
        patisseriesBistrot.setDisplayOrder(3);
        patisseriesBistrot.setActive(true);
        categoryRepository.save(patisseriesBistrot);
        ProductCategory ent = new ProductCategory();
        ent.setName("Entrées");
        ent.setDescription("Mises en bouche légères et salades.");
        ent.setRestaurant(bistrot);
        ent.setDisplayOrder(1);
        ent.setSupplierName("Maraîcher du Centre");
        ent.setSupplierContact("+221 77 123 4567");
        ent.setParentCategory(alimentsBistrot);
        categoryRepository.save(ent);

        ProductCategory plat = new ProductCategory();
        plat.setName("Plats de Résistance");
        plat.setDescription("Thiéboudienne, Yassa, Mafé et autres spécialités.");
        plat.setRestaurant(bistrot);
        plat.setDisplayOrder(2);
        plat.setSupplierName("Boucherie Dakaroise");
        plat.setSupplierContact("+221 77 987 6543");
        plat.setParentCategory(alimentsBistrot);
        categoryRepository.save(plat);

        ProductCategory drink = new ProductCategory();
        drink.setName("Boissons");
        drink.setDescription("Boissons fraîches locales et internationales.");
        drink.setRestaurant(bistrot);
        drink.setDisplayOrder(3);
        drink.setSupplierName("SOBOA (Sénégal)");
        drink.setSupplierContact("+221 33 839 1200");
        drink.setParentCategory(boissonsBistrot);
        categoryRepository.save(drink);

        ProductCategory dessert = new ProductCategory();
        dessert.setName("Desserts");
        dessert.setDescription("Douceurs sucrées de fin de repas.");
        dessert.setRestaurant(bistrot);
        dessert.setDisplayOrder(4);
        dessert.setSupplierName("Pâtisserie La Ruche");
        dessert.setSupplierContact("+221 77 555 4321");
        dessert.setParentCategory(patisseriesBistrot);
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
        ProductCategory alimentsDM = new ProductCategory();
        alimentsDM.setName("Aliments");
        alimentsDM.setDescription("Plats et repas principaux.");
        alimentsDM.setRestaurant(epieurien);
        alimentsDM.setDisplayOrder(1);
        alimentsDM.setActive(true);
        categoryRepository.save(alimentsDM);

        ProductCategory boissonsDM = new ProductCategory();
        boissonsDM.setName("Boissons");
        boissonsDM.setDescription("Boissons rafraîchissantes.");
        boissonsDM.setRestaurant(epieurien);
        boissonsDM.setDisplayOrder(2);
        boissonsDM.setActive(true);
        categoryRepository.save(boissonsDM);

        ProductCategory patisseriesDM = new ProductCategory();
        patisseriesDM.setName("Pâtisseries");
        patisseriesDM.setDescription("Gâteaux et desserts sucrés.");
        patisseriesDM.setRestaurant(epieurien);
        patisseriesDM.setDisplayOrder(3);
        patisseriesDM.setActive(true);
        categoryRepository.save(patisseriesDM);

        ProductCategory rotisCat = new ProductCategory();
        rotisCat.setName("Nos Rôtis");
        rotisCat.setDescription("Délicieux rôtis préparés avec soin.");
        rotisCat.setRestaurant(epieurien);
        rotisCat.setDisplayOrder(1);
        rotisCat.setSupplierName("Boucherie Divine");
        rotisCat.setSupplierContact("+237 699 99 99 99");
        rotisCat.setParentCategory(alimentsDM);
        categoryRepository.save(rotisCat);

        ProductCategory platsCat = new ProductCategory();
        platsCat.setName("Émincés & Plats");
        platsCat.setDescription("Spécialités de la maison.");
        platsCat.setRestaurant(epieurien);
        platsCat.setDisplayOrder(2);
        platsCat.setSupplierName("Marché Central de Douala");
        platsCat.setSupplierContact("+237 677 77 77 77");
        platsCat.setParentCategory(alimentsDM);
        categoryRepository.save(platsCat);

        ProductCategory fruitsCat = new ProductCategory();
        fruitsCat.setName("Desserts & Fruits");
        fruitsCat.setDescription("Douceurs fruitées et desserts.");
        fruitsCat.setRestaurant(epieurien);
        fruitsCat.setDisplayOrder(3);
        fruitsCat.setSupplierName("Verger du Noun");
        fruitsCat.setSupplierContact("+237 655 55 55 55");
        fruitsCat.setParentCategory(patisseriesDM);
        categoryRepository.save(fruitsCat);

        ProductCategory boissonsCat = new ProductCategory();
        boissonsCat.setName("Boissons Fraîches");
        boissonsCat.setDescription("Boissons locales et sodas.");
        boissonsCat.setRestaurant(epieurien);
        boissonsCat.setDisplayOrder(4);
        boissonsCat.setSupplierName("Brasseries du Cameroun");
        boissonsCat.setSupplierContact("+237 333 33 33 33");
        boissonsCat.setParentCategory(boissonsDM);
        categoryRepository.save(boissonsCat);

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

        Product jusFolere = new Product();
        jusFolere.setName("Jus de Foléré DM");
        jusFolere.setDescription("Jus d'hibiscus fait maison, parfumé à la menthe et à l'ananas.");
        jusFolere.setPrice(1000.0);
        jusFolere.setCategory(boissonsCat);
        jusFolere.setRestaurant(epieurien);
        jusFolere.setStockQuantity(50);
        jusFolere.setBeverage(true);
        productRepository.save(jusFolere);

        Product eauMinerale = new Product();
        eauMinerale.setName("Eau Minérale Semme");
        eauMinerale.setDescription("Eau minérale naturelle locale (1.5L).");
        eauMinerale.setPrice(500.0);
        eauMinerale.setCategory(boissonsCat);
        eauMinerale.setRestaurant(epieurien);
        eauMinerale.setStockQuantity(100);
        eauMinerale.setBeverage(true);
        productRepository.save(eauMinerale);

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
