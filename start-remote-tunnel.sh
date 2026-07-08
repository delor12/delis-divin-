#!/bin/bash

# Delis Divin - Remote Access & Exposure Utility Script
# This script helps you access your local Spring Boot server (port 8085) remotely.

# Colors for formatting
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}================================================================${NC}"
echo -e "${BLUE}          DELIS DIVIN - CONFIGURATION D'ACCÈS DISTANT           ${NC}"
echo -e "${BLUE}================================================================${NC}"
echo ""

# 1. Local LAN Network Access
echo -e "${YELLOW}[1/2] Option A : Accès sur votre réseau local (Wi-Fi / LAN)${NC}"
echo "Idéal pour tester l'application depuis votre téléphone ou tablette connectés au même réseau."
echo ""

# Retrieve local IP addresses (filtering out loopback)
LOCAL_IPS=$(hostname -I 2>/dev/null || ip route get 1.2.3.4 2>/dev/null | awk '{print $7}')
if [ -z "$LOCAL_IPS" ]; then
    LOCAL_IPS="IP_DE_VOTRE_ORDINATEUR"
fi

echo "Pour vous connecter, ouvrez le navigateur de votre appareil et saisissez :"
for ip in $LOCAL_IPS; do
    # Skip docker or virtual bridge interfaces if any
    if [[ ! $ip =~ ^172\. ]] && [[ ! $ip =~ ^10\.244\. ]]; then
        echo -e "   👉 ${GREEN}http://${ip}:8085${NC}"
    fi
done
echo ""

# 2. Public Internet Tunnel Access
echo -e "${YELLOW}[2/2] Option B : Accès public via Tunnel SSH (Sans installation)${NC}"
echo "Idéal pour partager un lien de démo utilisable n'importe où dans le monde sur Internet."
echo ""
echo "Choisissez une méthode ci-dessous pour ouvrir le tunnel :"
echo ""
echo -e "   ${BLUE}Méthode 1: Localhost.run (Recommandé)${NC}"
echo -e "   Exécutez dans un nouveau terminal :"
echo -e "   👉 ${GREEN}ssh -R 80:localhost:8085 nokey@localhost.run${NC}"
echo ""
echo -e "   ${BLUE}Méthode 2: Serveo.net${NC}"
echo -e "   Exécutez dans un nouveau terminal :"
echo -e "   👉 ${GREEN}ssh -R 80:localhost:8085 serveo.net${NC}"
echo ""
echo -e "   ${BLUE}Méthode 3: Localtunnel (via Node.js/npx)${NC}"
echo -e "   Exécutez dans un nouveau terminal :"
echo -e "   👉 ${GREEN}npx localtunnel --port 8085${NC}"
echo ""
echo -e "${BLUE}================================================================${NC}"
