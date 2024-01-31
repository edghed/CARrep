# CARrep
# Reponse à la question 3
Pour étendre mon programme de serveur FTP afin de supporter plus de commandes et mécanismes du protocole FTP, je pourrais d'abord étendre le traitement des commandes dans la classe ClientHandler. Cela impliquerait l'ajout de conditions if supplémentaires dans la méthode run pour reconnaître et traiter un plus grand nombre de commandes et j’ajouterais une méthode spécifique pour la gestion de chaque commande qui va comprendre la logique spécifique à chaque commande.

Mon serveur actuel prend en charge le mode passif pour la connexion de données, je pourrais aussi implémenter le mode actif. Dans ce mode c’est le serveur qui initie les connexions de données vers le client, cela nécessite la gestion des commandes PORT ou EPRT.

