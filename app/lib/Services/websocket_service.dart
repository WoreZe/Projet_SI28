import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:web_socket_channel/web_socket_channel.dart';
import 'authentification_service.dart';
import '/Environnement/env.dart';


enum TopicTypes {
  historyInstance,
}

class Message {
  final String type;
  final dynamic data;
  final int timestamp;

  Message({required this.type, required this.data, required this.timestamp});

  factory Message.fromJson(Map<String, dynamic> json) {
    return Message(
      type: json['type'],
      data: json['data'],
      timestamp: json['timestamp'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'type': type,
      'data': data,
      'timestamp': timestamp,
    };
  }
}

class WebsocketService with ChangeNotifier {
  WebSocketChannel? _channel;
  final AuthentificationService authService;

  final _messageSubject = StreamController<Message>();
  final _errorSubject = StreamController<Object>();
  final _isReadySubject = StreamController<bool>();

  final _messageBroker = <String, List<StreamController<Message?>>> {};

  Stream<Message> get messageStream => _messageSubject.stream;
  Stream<Object> get errorStream => _errorSubject.stream;
  Stream<bool> get isReadyStream => _isReadySubject.stream;

  WebsocketService._internal(this.authService) {
    authService.isAuth$.listen((isAuth) {
      if (!isAuth) {
        _channel?.sink.close();
        _channel = null;
      }
    });
  }

  WebSocketChannelConfig _getWsConfig() {
    final url = '${env.api}/history/instances_ws?token=${authService.getAccessToken()}';
    return WebSocketChannelConfig(url: url);
  }

  void connectToWebSocket() {
    final config = _getWsConfig();
    _channel = WebSocketChannel.connect(Uri.parse(config.url));

    _channel!.stream.listen(
      _onMessage,
      onError: _onWebSocketError,
      onDone: () {
        _channel = null;
        _onWebSocketError('Connection closed');
      },
    );
  }

  void _onMessage(dynamic message) {
    final parsedMessage = Message.fromJson(message as Map<String, dynamic>);
    _isReadySubject.add(true);
    _messageSubject.add(parsedMessage);
    _messageBroker[parsedMessage.type]?.forEach((subject) {
      if (!subject.isClosed) {
        subject.add(parsedMessage);
      }
    });
  }

  void _onWebSocketError(Object error) {
    _errorSubject.add(error);
    _isReadySubject.add(false);
  }

  void sendMessage(dynamic message) {
    _channel?.sink.add(message);
  }

  Stream<Message?> subscribeToMessage(TopicTypes name) {
    final topicName = name.name;
    _messageBroker.putIfAbsent(topicName, () => <StreamController<Message?>>[]);
    final subject = StreamController<Message?>();
    _messageBroker[topicName]?.add(subject);
    subject.onCancel = () {
      _messageBroker[topicName]?.remove(subject);
      subject.close();
    };
    return subject.stream;
  }

  void sendMessageTo(String name, dynamic message) {
    sendMessage({'name': name, 'data': message});
  }

  Stream<Message?> subscribeTo(TopicTypes name) {
    return subscribeToMessage(name);
  }

  @override
  void dispose() {
    _channel?.sink.close();
    _messageSubject.close();
    _errorSubject.close();
    _isReadySubject.close();
    super.dispose();
  }

  //singleton
  static final WebsocketService _instance =
      WebsocketService._internal(AuthentificationService.getInstance());
  static WebsocketService getInstance() => _instance;
}

class WebSocketChannelConfig {
  final String url;

  WebSocketChannelConfig({required this.url});
}

