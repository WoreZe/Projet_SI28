import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:circular_countdown_timer/circular_countdown_timer.dart';

class Question4Page extends StatefulWidget {
  const Question4Page({super.key});

  @override
  State<Question4Page> createState() => _QuestionPageState();
}

class _QuestionPageState extends State<Question4Page> {
  final _countDownController = CountDownController();
  String answer1_1 = "Partir explorer le monde des nuages";
  String answer1_2 = "S'approcher du bord";
  String answer2_1 = "Les sauver et continuer ensemble";
  String answer2_2 = "Finir l'aventure sans eux";
  String textQuestion1 = "Ce farceur d'Edmond a décidé de vous téléporter dans le monde des nuages, cet endroit est magnifique !!! Prenez garde les nuages sont imprévisibles...";
  String textQuestion2 = "S'approcher du bord permet de mieux comprendre la situation, mais joueurrandom1 et joueurrandom2 tombent, VITE IL FAUT PRENDRE UNE DECISION !!!!";
  bool isFirstQuestion = true;
  bool isBorder = false;
  bool isCountdown = false;
  int _counter = 0;

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
                  duration: 15,
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
                  autoStart: false,
                ),
              ),
              Container(
                color: Colors.orange, // Couleur de la boîte
                child: SizedBox(
                  height: 200, // Définir la hauteur souhaitée
                  child: Center(
                    child: Text(
                      getQuestion(),
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
                            backgroundColor: MaterialStateProperty.all<Color>(Colors.white), // Changer la couleur du bouton
                          ),
                          onPressed: () {
                            setState(() {
                              isFirstQuestion = false;
                              isBorder = true;
                              if (isBorder && !isCountdown) {
                                isCountdown = true;
                                _countDownController.start();
                              }
                              if (isBorder){
                                _counter++;
                              }
                            });
                          },
                          child: Center(
                            child: Text(
                              getAnswer1(),
                              style: TextStyle(color: Colors.black, fontFamily: 'madimi', fontSize: 24),
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
                            backgroundColor: MaterialStateProperty.all<Color>(Colors.white), // Changer la couleur du bouton
                          ),
                          onPressed: () {
                            setState(() {
                              isFirstQuestion = false;
                              isBorder = true;
                              if (isBorder && !isCountdown) {
                                isCountdown = true;
                                _countDownController.start();
                              }
                              if (isBorder){
                                _counter++;
                              }
                            });
                          },
                          child: Center(
                            child: Text(
                              getAnswer2(),
                              style: TextStyle(color: Colors.black, fontFamily: 'madimi', fontSize: 24),
                            ),
                          ),
                        ),
                      ),
                    ),

                  ],
                ),
              ),


            ])
    );

  }
  String getQuestion() {
    if (isFirstQuestion) {
      return textQuestion1;
    } else if (isBorder) {
      return textQuestion2;
    } else {
      return 'Aucune variable n\'est vraie';
    }
  }

  String getAnswer1() {
    if (isFirstQuestion) {
      return answer1_1;
    } else if (isBorder) {
      return answer2_1;
    } else {
      return 'Aucune variable n\'est vraie';
    }
  }

  String getAnswer2() {
    if (isFirstQuestion) {
      return answer1_2;
    } else if (isBorder) {
      return answer2_2;
    } else {
      return 'Aucune variable n\'est vraie';
    }
  }
}
