import 'package:flutter/material.dart';

class Todo {
  final String id;
  final String title;
  final bool isCompleted;
  final DateTime createdAt;

  Todo({
    required this.id,
    required this.title,
    this.isCompleted = false,
    required this.createdAt,
  });

  Todo copyWith({
    String? id,
    String? title,
    bool? isCompleted,
    DateTime? createdAt,
  }) {
    return Todo(
      id: id ?? this.id,
      title: title ?? this.title,
      isCompleted: isCompleted ?? this.isCompleted,
      createdAt: createdAt ?? this.createdAt,
    );
  }
}

class TodoProvider extends ChangeNotifier {
  final List<Todo> _todos = [];

  List<Todo> get todos => List.unmodifiable(_todos);
  List<Todo> get completedTodos => _todos.where((t) => t.isCompleted).toList();
  List<Todo> get pendingTodos => _todos.where((t) => !t.isCompleted).toList();
  int get totalCount => _todos.length;
  int get completedCount => completedTodos.length;

  void addTodo(String title) {
    if (title.trim().isEmpty) return;
    _todos.insert(
      0,
      Todo(
        id: DateTime.now().millisecondsSinceEpoch.toString(),
        title: title.trim(),
        createdAt: DateTime.now(),
      ),
    );
    notifyListeners();
  }

  void toggleTodo(String id) {
    final index = _todos.indexWhere((t) => t.id == id);
    if (index != -1) {
      _todos[index] = _todos[index].copyWith(
        isCompleted: !_todos[index].isCompleted,
      );
      notifyListeners();
    }
  }

  void deleteTodo(String id) {
    _todos.removeWhere((t) => t.id == id);
    notifyListeners();
  }
}
