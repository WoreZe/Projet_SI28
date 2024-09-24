import 'dart:async';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../Environnement/env.dart';

class AuthentificationService {
  final StreamController<bool> _isAuth$ = StreamController<bool>.broadcast();

  Stream<bool> get isAuth$ => _isAuth$.stream;

  AuthentificationService() {
    _initAuthStatus();
  }

  Future<void> _initAuthStatus() async {
    _isAuth$.add(await isAuth());
  }

  Future<void> login(String code, String username, String avatarUrl) async {
    final response = await http.post(
      Uri.parse('${env.api}/auth/login/history'),
      headers: <String, String>{
        'Content-Type': 'application/json; charset=UTF-8',
      },
      body: jsonEncode(<String, dynamic>{
        'code': code,
        'username': username,
        'avatar': avatarUrl,
        'screen': true,
      }),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString('access', data['accessToken']);
      _isAuth$.add(true);
    } else {
      throw Exception('Failed to login');
    }
  }

  Future<String?> getAccessToken() async {
    final prefs = await SharedPreferences.getInstance();
    _isAuth$.add(await isAuth());
    return prefs.getString('access');
  }

  Future<bool> isAuth() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString('access') != null;
  }

  Future<void> signOut() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('access');
    _isAuth$.add(false);
  }

  void dispose() {
    _isAuth$.close();
  }

  //singleton
  static final AuthentificationService _instance =
      AuthentificationService._internal();

  factory AuthentificationService.getInstance() => _instance;

  AuthentificationService._internal();
}