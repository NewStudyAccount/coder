// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user_preferences_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$UserPreferencesModelImpl _$$UserPreferencesModelImplFromJson(
  Map<String, dynamic> json,
) => _$UserPreferencesModelImpl(
  themeMode: json['themeMode'] as String? ?? 'system',
  language: json['language'] as String? ?? 'zh',
);

Map<String, dynamic> _$$UserPreferencesModelImplToJson(
  _$UserPreferencesModelImpl instance,
) => <String, dynamic>{
  'themeMode': instance.themeMode,
  'language': instance.language,
};
