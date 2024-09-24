import 'package:app/Pages/home_page.dart';
import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'Pages/test_page.dart';
import 'Services/camera_services.dart';


Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await CameraService.instance.initialize();
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  int _currentIndex = 0;

  setCurrentIndex(int index) {
    setState(() {
      _currentIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData.dark().copyWith(
        scaffoldBackgroundColor: Color(0xFF353535),
        textTheme:TextTheme(
          bodyMedium: TextStyle(
            fontFamily: 'Madimi'
          )
        )
      ),

      home: Scaffold(
        appBar: AppBar(
          title: Text("CoScén", style: TextStyle(fontFamily: 'Madimi', fontSize: 48)),
          centerTitle: true,
          backgroundColor: Color(0xFF4B4B4B)
        ),
        body: [
          HomePage(),
          TestPage()
        ][_currentIndex],
        bottomNavigationBar: BottomNavigationBar(
          backgroundColor: Color(0xFF4B4B4B),
          currentIndex: _currentIndex,
          onTap: (index) => setCurrentIndex(index),
          selectedItemColor: Colors.orange,
          unselectedItemColor: Colors.grey,
          elevation: 10,
          items: const[
            BottomNavigationBarItem(
                icon: Icon(Icons.home),
                label: 'Accueil'
            ),
            BottomNavigationBarItem(
                icon: Icon(Icons.all_inclusive),
                label: 'Test'
            )
          ],
        ),
      ),
    );
  }
}


