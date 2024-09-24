import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:circular_countdown_timer/circular_countdown_timer.dart';

class QuestionPage extends StatefulWidget {
  const QuestionPage({super.key});

  @override
  State<QuestionPage> createState() => _QuestionPageState();
}

class _QuestionPageState extends State<QuestionPage> {
  final _countDownController = CountDownController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: Text("Nom de l'histoire", style: TextStyle(fontFamily: 'madimi')),
        ),
        body: Column(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              Container(
                padding: EdgeInsets.all(16.0),
                child: CircularCountDownTimer(
                  controller: _countDownController,
                  duration: 30,
                  isReverse: true,
                  fillColor: Colors.orange,
                  backgroundColor: Colors.orange,
                  height: 50,
                  width: 50,
                  onComplete: () {
                    // Here, do whatever you want
                    debugPrint('Countdown Ended');
                  },
                  isReverseAnimation: true,
                  ringColor: Colors.grey,
                  autoStart: true,
                ),
              ),
              Container(
                color: Colors.orange, // Couleur de la boîte
                child: SizedBox(
                  height: 200, // Définir la hauteur souhaitée
                  child: Center(
                    child: Text(
                      'Votre question qui peut être un peu longue si vous voulez',
                      textAlign: TextAlign.center, // Centrer le texte horizontalement
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 24,
                      ), // Couleur du texte
                    ),
                  ),
                ),
              ),

              Container(
                height: 400, // Hauteur du conteneur principal
                child: Column(
                  children: [
                    Expanded(
                      child: Padding(
                        padding: EdgeInsets.symmetric(vertical: 8.0), // Padding vertical entre les parties
                        child: ElevatedButton(
                          style: ButtonStyle(
                            backgroundColor: MaterialStateProperty.all<Color>(Colors.red), // Changer la couleur du bouton
                          ),
                          onPressed: () {
                            // Actions à effectuer lorsque le bouton est appuyé
                          },
                          child: Center(
                            child: Text(
                              'Texte 1',
                              style: TextStyle(color: Colors.white, fontFamily: 'madimi'),
                            ),
                          ),
                        ),
                      ),
                    ),
                    Expanded(
                      child: Padding(
                        padding: EdgeInsets.symmetric(vertical: 8.0), // Padding vertical entre les parties
                        child: ElevatedButton(
                          style: ButtonStyle(
                            backgroundColor: MaterialStateProperty.all<Color>(Colors.blue), // Changer la couleur du bouton
                          ),
                          onPressed: () {
                            // Actions à effectuer lorsque le bouton est appuyé
                          },
                          child: Center(
                            child: Text(
                              'Texte 1',
                              style: TextStyle(color: Colors.white, fontFamily: 'madimi'),
                            ),
                          ),
                        ),
                      ),
                    ),
                    Expanded(
                      child: Padding(
                        padding: EdgeInsets.symmetric(vertical: 8.0), // Padding vertical entre les parties
                        child: ElevatedButton(
                          style: ButtonStyle(
                            backgroundColor: MaterialStateProperty.all<Color>(Colors.green), // Changer la couleur du bouton
                          ),
                          onPressed: () {
                            // Actions à effectuer lorsque le bouton est appuyé
                          },
                          child: Center(
                            child: Text(
                              'Texte 1',
                              style: TextStyle(color: Colors.white, fontFamily: 'madimi'),
                            ),
                          ),
                        ),
                      ),
                    ),
                    Expanded(
                      child: Padding(
                        padding: EdgeInsets.symmetric(vertical: 8.0), // Padding vertical entre les parties
                        child: ElevatedButton(
                          style: ButtonStyle(
                            backgroundColor: MaterialStateProperty.all<Color>(Colors.yellow), // Changer la couleur du bouton
                          ),
                          onPressed: () {
                            // Actions à effectuer lorsque le bouton est appuyé
                          },
                          child: Center(
                            child: Text(
                              'Texte 1',
                              style: TextStyle(color: Colors.white, fontFamily: 'madimi'),
                            ),
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ),


            ]));
  }
}
