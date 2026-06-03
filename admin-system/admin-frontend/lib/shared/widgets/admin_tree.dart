import 'package:flutter/material.dart';

class AdminTreeNode<T> {
  final T data;
  final String label;
  final String? key;
  final List<AdminTreeNode<T>> children;
  bool isExpanded;
  bool isSelected;

  AdminTreeNode({
    required this.data,
    required this.label,
    this.key,
    this.children = const [],
    this.isExpanded = false,
    this.isSelected = false,
  });
}

class AdminTree<T> extends StatelessWidget {
  final List<AdminTreeNode<T>> nodes;
  final ValueChanged<AdminTreeNode<T>>? onNodeTap;
  final ValueChanged<AdminTreeNode<T>>? onNodeSelect;
  final Widget Function(AdminTreeNode<T>, int level)? iconBuilder;
  final bool showCheckbox;
  final bool expandAll;
  final String? searchQuery;
  final EdgeInsets padding;

  const AdminTree({
    super.key,
    required this.nodes,
    this.onNodeTap,
    this.onNodeSelect,
    this.iconBuilder,
    this.showCheckbox = false,
    this.expandAll = false,
    this.searchQuery,
    this.padding = const EdgeInsets.symmetric(horizontal: 8),
  });

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      padding: padding,
      itemCount: nodes.length,
      itemBuilder: (context, index) {
        return _buildNode(context, nodes[index], 0);
      },
    );
  }

  Widget _buildNode(BuildContext context, AdminTreeNode<T> node, int level) {
    final hasChildren = node.children.isNotEmpty;
    final matchesSearch = searchQuery == null ||
        searchQuery!.isEmpty ||
        node.label.toLowerCase().contains(searchQuery!.toLowerCase());

    if (!matchesSearch && !_hasMatchingChild(node)) {
      return const SizedBox.shrink();
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        InkWell(
          onTap: () {
            if (hasChildren) {
              node.isExpanded = !node.isExpanded;
            }
            onNodeTap?.call(node);
          },
          child: Padding(
            padding: EdgeInsets.only(left: level * 24.0),
            child: Row(
              children: [
                if (hasChildren)
                  AnimatedRotation(
                    turns: node.isExpanded || expandAll ? 0.25 : 0,
                    duration: const Duration(milliseconds: 200),
                    child: Icon(
                      Icons.chevron_right,
                      size: 20,
                      color: Theme.of(context).hintColor,
                    ),
                  )
                else
                  const SizedBox(width: 20),
                if (showCheckbox)
                  Checkbox(
                    value: node.isSelected,
                    onChanged: (val) {
                      node.isSelected = val ?? false;
                      onNodeSelect?.call(node);
                    },
                  ),
                if (iconBuilder != null)
                  Padding(
                    padding: const EdgeInsets.only(right: 8),
                    child: iconBuilder!(node, level),
                  ),
                Expanded(
                  child: Text(
                    node.label,
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          fontWeight: level == 0 ? FontWeight.w600 : FontWeight.normal,
                        ),
                  ),
                ),
              ],
            ),
          ),
        ),
        if (hasChildren && (node.isExpanded || expandAll))
          ...node.children.map((child) => _buildNode(context, child, level + 1)),
      ],
    );
  }

  bool _hasMatchingChild(AdminTreeNode<T> node) {
    for (final child in node.children) {
      if (child.label.toLowerCase().contains((searchQuery ?? '').toLowerCase())) {
        return true;
      }
      if (_hasMatchingChild(child)) return true;
    }
    return false;
  }
}
