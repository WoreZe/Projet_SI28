import 'package:avataaars/domain/avataaar.dart';
import 'package:flutter/material.dart';

import 'avatar_generator.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  late String _inputCode = ''; // Variable pour stocker le code
  late String _inputPseudo =''; // Variable pour stocker le pseudo
  late Map<String, dynamic> json_a; // Variable pour stocker l'avatar

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: <Widget>[
        Padding(
          padding: EdgeInsets.all(16.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                "Veuillez entrer le code d'une histoire",
                style: TextStyle(fontSize: 18),
              ),
              TextField(
                onChanged: (value) {
                  setState(() {
                    _inputCode = value; // Mettre à jour le texte saisi à chaque changement
                  });
                },
                style: TextStyle(color: Colors.black, fontFamily: 'Madimi'),
                decoration: InputDecoration(
                  filled: true, // Définir le remplissage comme activé
                  fillColor: Colors.white, // Couleur de remplissage blanc
                  border: OutlineInputBorder( // Définir les bordures
                    borderRadius: BorderRadius.circular(10.0),
                    borderSide: BorderSide(color: Colors.grey), // Couleur de la bordure lorsqu'elle est désactivée
                  ),
                  enabledBorder: OutlineInputBorder( // Définir la bordure activée
                    borderRadius: BorderRadius.circular(10.0),
                    borderSide: BorderSide(color: Colors.grey), // Couleur de la bordure activée
                  ),
                  focusedBorder: OutlineInputBorder( // Définir la bordure en focus
                    borderRadius: BorderRadius.circular(10.0),
                    borderSide: BorderSide(color: Colors.blue), // Couleur de la bordure en focus
                  ),
                ),
              ),
              SizedBox(height: 20),
              Text(
                "Veuillez entrer un pseudo",
                style: TextStyle(fontSize: 18),
              ),
              TextField(
                onChanged: (value) {
                  setState(() {
                    _inputPseudo = value; // Mettre à jour le texte saisi à chaque changement
                  });
                },
                style: TextStyle(color: Colors.black, fontFamily: 'Madimi'),
                decoration: InputDecoration(
                  filled: true, // Définir le remplissage comme activé
                  fillColor: Colors.white, // Couleur de remplissage blanc
                  border: OutlineInputBorder( // Définir les bordures
                    borderRadius: BorderRadius.circular(10.0),
                    borderSide: BorderSide(color: Colors.grey), // Couleur de la bordure lorsqu'elle est désactivée
                  ),
                  enabledBorder: OutlineInputBorder( // Définir la bordure activée
                    borderRadius: BorderRadius.circular(10.0),
                    borderSide: BorderSide(color: Colors.grey), // Couleur de la bordure activée
                  ),
                  focusedBorder: OutlineInputBorder( // Définir la bordure en focus
                    borderRadius: BorderRadius.circular(10.0),
                    borderSide: BorderSide(color: Colors.blue), // Couleur de la bordure en focus
                  ),
                ),
              ),
              SizedBox(height: 20),
              Text(
                'Code saisi : $_inputCode',
                style: TextStyle(fontSize: 18, color: Colors.orange),
              ),
              Text(
                'Pseudo saisi : $_inputPseudo',
                style: TextStyle(fontSize: 18, color: Colors.orange),
              ),
              ElevatedButton(
                style: ButtonStyle(
                  backgroundColor: MaterialStateProperty.all<Color>(Colors.orange), // Changer la couleur du bouton
                ),
                onPressed: () {
                  // Actions à effectuer lorsque le bouton est appuyé
                },
                child: Center(
                  child: Text(
                    'Valider',
                    style: TextStyle(color: Colors.white, fontFamily: 'madimi'),
                  ),
                ),
              ),
            ],
          ),
        ),
        Positioned(
          bottom: 16,
          right: 16,
          child: ElevatedButton(
            style: ButtonStyle(
              backgroundColor: MaterialStateProperty.all<Color>(Colors.orange), // Changer la couleur du bouton
            ),
            onPressed: () async {
              Avataaar a = await Navigator.push(
                context,
                MaterialPageRoute(builder: (ctx) => UserProfilAvatar()),
              );
              json_a = a.toJson();
              print(json_a);
            },
            child: Icon(
              Icons.account_circle_rounded,
              color: Colors.white,
            ),
          ),
        ),
      ],
    );
  }
}
