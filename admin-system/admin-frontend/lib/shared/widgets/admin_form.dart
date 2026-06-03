import 'package:flutter/material.dart';

class AdminForm extends StatelessWidget {
  final List<AdminFormField> fields;
  final GlobalKey<FormState> formKey;
  final Map<String, dynamic> initialValues;
  final ValueChanged<Map<String, dynamic>>? onSubmit;
  final String submitLabel;
  final bool isLoading;

  const AdminForm({
    super.key,
    required this.fields,
    required this.formKey,
    this.initialValues = const {},
    this.onSubmit,
    this.submitLabel = '提交',
    this.isLoading = false,
  });

  @override
  Widget build(BuildContext context) {
    return Form(
      key: formKey,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          ...fields.map((field) => _buildField(context, field)),
          const SizedBox(height: 24),
          if (onSubmit != null)
            SizedBox(
              width: double.infinity,
              child: FilledButton(
                onPressed: isLoading ? null : () => _handleSubmit(context),
                child: isLoading
                    ? const SizedBox(
                        height: 20,
                        width: 20,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    : Text(submitLabel),
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildField(BuildContext context, AdminFormField field) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 16),
      child: switch (field.type) {
        AdminFormFieldType.text => _buildTextField(context, field),
        AdminFormFieldType.number => _buildNumberField(context, field),
        AdminFormFieldType.password => _buildPasswordField(context, field),
        AdminFormFieldType.select => _buildSelectField(context, field),
        AdminFormFieldType.date => _buildDateField(context, field),
        AdminFormFieldType.datetime => _buildDateField(context, field, showTime: true),
        AdminFormFieldType.textarea => _buildTextField(context, field, maxLines: 4),
        AdminFormFieldType.checkbox => _buildCheckboxField(context, field),
        AdminFormFieldType.radio => _buildRadioField(context, field),
        AdminFormFieldType.switch_ => _buildSwitchField(context, field),
      },
    );
  }

  Widget _buildTextField(BuildContext context, AdminFormField field, {int maxLines = 1}) {
    return TextFormField(
      initialValue: initialValues[field.name]?.toString() ?? field.defaultValue ?? '',
      decoration: InputDecoration(
        labelText: field.label,
        hintText: field.placeholder,
        prefixIcon: field.icon != null ? Icon(field.icon) : null,
        border: const OutlineInputBorder(),
      ),
      maxLines: maxLines,
      validator: field.required
          ? (val) => val == null || val.isEmpty ? '${field.label}不能为空' : null
          : null,
      onSaved: (val) => initialValues[field.name] = val,
    );
  }

  Widget _buildNumberField(BuildContext context, AdminFormField field) {
    return TextFormField(
      initialValue: initialValues[field.name]?.toString() ?? field.defaultValue ?? '',
      decoration: InputDecoration(
        labelText: field.label,
        hintText: field.placeholder,
        prefixIcon: field.icon != null ? Icon(field.icon) : null,
        border: const OutlineInputBorder(),
      ),
      keyboardType: TextInputType.number,
      validator: field.required
          ? (val) => val == null || val.isEmpty ? '${field.label}不能为空' : null
          : null,
      onSaved: (val) => initialValues[field.name] = num.tryParse(val ?? ''),
    );
  }

  Widget _buildPasswordField(BuildContext context, AdminFormField field) {
    return TextFormField(
      initialValue: initialValues[field.name]?.toString() ?? '',
      decoration: InputDecoration(
        labelText: field.label,
        hintText: field.placeholder,
        prefixIcon: field.icon != null ? Icon(field.icon) : null,
        border: const OutlineInputBorder(),
      ),
      obscureText: true,
      validator: field.required
          ? (val) => val == null || val.isEmpty ? '${field.label}不能为空' : null
          : null,
      onSaved: (val) => initialValues[field.name] = val,
    );
  }

  Widget _buildSelectField(BuildContext context, AdminFormField field) {
    return DropdownButtonFormField<String>(
      value: initialValues[field.name]?.toString() ?? field.defaultValue,
      decoration: InputDecoration(
        labelText: field.label,
        prefixIcon: field.icon != null ? Icon(field.icon) : null,
        border: const OutlineInputBorder(),
      ),
      items: field.options
          .map((opt) => DropdownMenuItem(
                value: opt.value,
                child: Text(opt.label),
              ))
          .toList(),
      validator: field.required ? (val) => val == null ? '${field.label}不能为空' : null : null,
      onChanged: (val) => initialValues[field.name] = val,
      onSaved: (val) => initialValues[field.name] = val,
    );
  }

  Widget _buildDateField(BuildContext context, AdminFormField field, {bool showTime = false}) {
    return TextFormField(
      initialValue: initialValues[field.name]?.toString() ?? '',
      decoration: InputDecoration(
        labelText: field.label,
        prefixIcon: const Icon(Icons.calendar_today),
        border: const OutlineInputBorder(),
      ),
      readOnly: true,
      onTap: () async {
        final date = await showDatePicker(
          context: context,
          initialDate: DateTime.now(),
          firstDate: DateTime(2000),
          lastDate: DateTime(2100),
        );
        if (date != null) {
          initialValues[field.name] = date.toIso8601String().split('T')[0];
        }
      },
      validator: field.required
          ? (val) => val == null || val.isEmpty ? '${field.label}不能为空' : null
          : null,
      onSaved: (val) => initialValues[field.name] = val,
    );
  }

  Widget _buildCheckboxField(BuildContext context, AdminFormField field) {
    return FormField<bool>(
      initialValue: initialValues[field.name] == true || field.defaultValue == 'true',
      builder: (state) {
        return CheckboxListTile(
          title: Text(field.label),
          value: state.value,
          onChanged: (val) {
            state.didChange(val);
            initialValues[field.name] = val;
          },
        );
      },
      onSaved: (val) => initialValues[field.name] = val,
    );
  }

  Widget _buildRadioField(BuildContext context, AdminFormField field) {
    return FormField<String>(
      initialValue: initialValues[field.name]?.toString() ?? field.defaultValue,
      builder: (state) {
        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(field.label, style: Theme.of(context).textTheme.bodySmall),
            ...field.options.map((opt) => RadioListTile<String>(
                  title: Text(opt.label),
                  value: opt.value,
                  groupValue: state.value,
                  onChanged: (val) {
                    state.didChange(val);
                    initialValues[field.name] = val;
                  },
                )),
          ],
        );
      },
      onSaved: (val) => initialValues[field.name] = val,
    );
  }

  Widget _buildSwitchField(BuildContext context, AdminFormField field) {
    return FormField<bool>(
      initialValue: initialValues[field.name] == true || field.defaultValue == 'true',
      builder: (state) {
        return SwitchListTile(
          title: Text(field.label),
          value: state.value ?? false,
          onChanged: (val) {
            state.didChange(val);
            initialValues[field.name] = val;
          },
        );
      },
      onSaved: (val) => initialValues[field.name] = val,
    );
  }

  void _handleSubmit(BuildContext context) {
    if (formKey.currentState!.validate()) {
      formKey.currentState!.save();
      onSubmit?.call(Map.from(initialValues));
    }
  }
}

enum AdminFormFieldType {
  text,
  number,
  password,
  select,
  date,
  datetime,
  textarea,
  checkbox,
  radio,
  switch_,
}

class AdminFormOption {
  final String label;
  final String value;

  const AdminFormOption({required this.label, required this.value});
}

class AdminFormField {
  final String name;
  final String label;
  final AdminFormFieldType type;
  final bool required;
  final String? placeholder;
  final String? defaultValue;
  final IconData? icon;
  final List<AdminFormOption> options;

  const AdminFormField({
    required this.name,
    required this.label,
    this.type = AdminFormFieldType.text,
    this.required = false,
    this.placeholder,
    this.defaultValue,
    this.icon,
    this.options = const [],
  });
}
