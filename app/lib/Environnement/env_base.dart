class EnvBase {
  final bool production;
  final String api;
  final String version;

  const EnvBase({
    required this.production,
    required this.api,
    required this.version,
  });

  factory EnvBase.fromJson(Map<String, dynamic> json) {
    return EnvBase(
      production: json['production'],
      api: json['api'],
      version: json['version'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'production': production,
      'api': api,
      'version': version,
    };
  }
}