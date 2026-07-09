# 🍽️ Delis Divin SaaS - Révolution Digitale de la Restauration en Afrique

**Delis Divin** est une plateforme SaaS multi-restaurant innovante conçue pour moderniser, digitaliser et optimiser la gestion des restaurants, bistrots et services de livraison. Alliant simplicité pour le client et outils de gestion professionnels pour le personnel, elle répond parfaitement aux réalités et opportunités du marché africain.

---

## 🌍 Les Avantages Clés pour le Marché Africain

La restauration en Afrique connaît une croissance rapide mais fait face à des défis uniques (connectivité, habitudes de paiement, logistique). **Delis Divin** y répond grâce à plusieurs innovations majeures :

### 1. 📲 Zéro Friction pour le Client (Sans Inscription)
* **Pas d'application à télécharger** : Le client scanne simplement un QR Code sur sa table pour accéder au menu interactif.
* **Pas de compte à créer** : Pour passer une commande ou chatter avec un serveur, le client n'a pas besoin de renseigner d'identifiant. L'expérience est instantanée, fluide et respecte la rapidité d'exécution recherchée.

### 💳 2. Intégration Native des Paiements Mobiles (Mobile Money)
Face à la faible pénétration des cartes bancaires, la plateforme intègre les solutions de paiement mobile leaders en Afrique de l'Ouest et Centrale :
* **Wave** 🌊 (Sénégal, Côte d'Ivoire...)
* **Free Money** 💸
* **Orange Money** 🍊
* **MTN Mobile Money** 📱
* Les encaissements en **espèces (Cash)** et par **carte** restent entièrement gérés et centralisés sur l'interface caissier.

### 🛵 3. Gestion Logistique Flexible (Livreurs Globaux et Internes)
* Le Super Admin peut enregistrer des livreurs indépendants rattachés à la plateforme globale ou dédiés à un restaurant spécifique.
* Optimisation de la livraison de proximité pour s'adapter au secteur informel de la livraison urbaine.

### 💬 4. Chat interactif Client ↔ Personnel en Temps Réel
* Un widget de chat direct et anonyme permet au client (sur place ou en livraison) de poser des questions, de demander des précisions sur le menu ou d'appeler le service.
* Les serveurs et le gérant reçoivent instantanément des alertes sonores et visuelles en WebSocket pour répondre rapidement.

---

## 🚀 Fonctionnalités Majeures

### 💻 Les Différents Espaces de Travail
1. **Espace Super Administrateur** : 
   * Création et gestion des restaurants partenaires de la plateforme.
   * Gestion globale des villes et des livreurs.
   * Déclenchement et supervision des sauvegardes de la base de données.
2. **Espace Admin de Restaurant** :
   * Gestion complète du menu, des catégories et du stock.
   * Gestion des employés (serveurs, chefs, caissiers).
   * Suivi des discussions clients et des commandes.
3. **Espace Serveur (Waiter Dashboard)** :
   * Réception en temps réel des commandes à servir.
   * Chat direct avec les clients attablés.
4. **Espace Chef (Kitchen Dashboard)** :
   * Visualisation en direct des plats à préparer avec gestion des priorités et mise à jour des statuts.
5. **Espace Caissier (Cashier Dashboard)** :
   * Encaissement des commandes avec sélection des modes de paiement locaux (Wave, Free Money, etc.) et édition des factures.
6. **Espace Livreur (Delivery Dashboard)** :
   * Suivi GPS des livraisons en cours et validation des étapes.

---

## 🛠️ Pile Technique

L'architecture est robuste, moderne et optimisée pour des déploiements économiques et performants :
* **Backend** : Java 21, Spring Boot 3.x, Spring Security (sécurité renforcée par rôles et authentification basée sur jetons JWT via cookies sécurisés).
* **Base de données** : Hibernate ORM, JPA (seeding automatique de démo, compatible MySQL/PostgreSQL/H2).
* **Temps Réel** : WebSockets via protocole STOMP et SockJS (assurant une reconnexion automatique en cas de coupure réseau mobile).
* **Frontend** : HTML5 (Thymeleaf), CSS3 premium (design fluide, responsive et épuré), JavaScript Vanilla.
* **DevOps & Tunnels** : Docker, Docker Compose, Nginx, scripts de tunneling auto-guérisseurs (Serveo).

---

## ⚙️ Démarrage Rapide

### 1. Prérequis
* Java 21+
* Maven 3.x

### 2. Lancement Local
Clonez le dépôt, compilez et lancez l'application :
```bash
mvn clean spring-boot:run
```
L'application démarre par défaut sur le port `8085`.

### 3. Connexion au Panneau de Contrôle
Rendez-vous sur `http://localhost:8085/login` :
* **Super Admin** : `superadmin@delisdivin.com` / `password`
* **Admin Resto** : Créez votre restaurant depuis l'espace Super Admin pour obtenir vos accès personnalisés.
