import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:circular_countdown_timer/circular_countdown_timer.dart';

class Question1Page extends StatefulWidget {
  const Question1Page({super.key});

  @override
  State<Question1Page> createState() => _Question1PageState();
}

class _Question1PageState extends State<Question1Page> {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text("Nom de l'histoire", style: TextStyle(fontFamily: 'madimi')),
        ),
        body: Padding(
          padding: EdgeInsets.all(16.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Container(
                decoration: BoxDecoration(
                  color: Colors.orange,
                  borderRadius: BorderRadius.circular(10.0),// Définit la couleur de fond en orange
                ),
                 // Définit la largeur de la boîte contenant le texte
                padding: EdgeInsets.all(10),
                margin: EdgeInsets.only(bottom: 20.0),
                // Ajoute un remplissage intérieur
                child: Center(
                  child: Text(
                    "Saisissez l'ancien nom de Maitre Edmond",
                    style: TextStyle(
                      fontSize: 24,
                      color: Colors.white, // Définit la couleur du texte en blanc
                    ),
                  ),
                )
              ),
              TextField(
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
        )

    );
  }
}
