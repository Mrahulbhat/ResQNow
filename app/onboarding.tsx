import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, StyleSheet, KeyboardAvoidingView, Platform, ScrollView } from 'react-native';
import { useRouter } from 'expo-router';
import { useSafety } from '../context/SafetyContext';
import { Theme } from '../constants/Theme';
import { Ionicons } from '@expo/vector-icons';

export default function OnboardingScreen() {
  const { updateSettings } = useSafety();
  const router = useRouter();
  const [form, setForm] = useState({
    emergencyContactName: '',
    emergencyContactPhone: '',
    policeStationPhone: '',
  });

  const handleSave = async () => {
    if (!form.emergencyContactPhone || !form.policeStationPhone) {
      alert('Please fill in both emergency contact and police station numbers.');
      return;
    }
    await updateSettings(form);
    router.replace('/(tabs)');
  };

  return (
    <KeyboardAvoidingView 
      behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
      style={styles.container}
    >
      <ScrollView contentContainerStyle={styles.scrollContent}>
        <View style={styles.header}>
          <View style={styles.logoContainer}>
            <Ionicons name="shield-checkmark" size={60} color={Theme.colors.primary} />
          </View>
          <Text style={styles.title}>ResQNow</Text>
          <Text style={styles.subtitle}>Your personal safety companion. Set up your emergency details to get started.</Text>
        </View>

        <View style={styles.form}>
          <View style={styles.inputGroup}>
            <Text style={styles.label}>Parent/Guardian Name</Text>
            <TextInput
              style={styles.input}
              placeholder="e.g. John Doe"
              placeholderTextColor={Theme.colors.textDim}
              value={form.emergencyContactName}
              onChangeText={(text) => setForm({ ...form, emergencyContactName: text })}
            />
          </View>

          <View style={styles.inputGroup}>
            <Text style={styles.label}>Parent/Guardian Phone</Text>
            <TextInput
              style={styles.input}
              placeholder="e.g. +1234567890"
              placeholderTextColor={Theme.colors.textDim}
              keyboardType="phone-pad"
              value={form.emergencyContactPhone}
              onChangeText={(text) => setForm({ ...form, emergencyContactPhone: text })}
            />
          </View>

          <View style={styles.inputGroup}>
            <Text style={styles.label}>Local Police Station Number</Text>
            <TextInput
              style={styles.input}
              placeholder="e.g. 911 or Local ID"
              placeholderTextColor={Theme.colors.textDim}
              keyboardType="phone-pad"
              value={form.policeStationPhone}
              onChangeText={(text) => setForm({ ...form, policeStationPhone: text })}
            />
          </View>

          <TouchableOpacity style={styles.button} onPress={handleSave}>
            <Text style={styles.buttonText}>Complete Setup</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Theme.colors.background,
  },
  scrollContent: {
    padding: Theme.spacing.xl,
    paddingTop: 80,
  },
  header: {
    alignItems: 'center',
    marginBottom: Theme.spacing.xl,
  },
  logoContainer: {
    width: 100,
    height: 100,
    borderRadius: 50,
    backgroundColor: 'rgba(255, 59, 48, 0.1)',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: Theme.spacing.md,
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: Theme.colors.text,
    marginBottom: Theme.spacing.xs,
  },
  subtitle: {
    fontSize: 16,
    color: Theme.colors.textDim,
    textAlign: 'center',
    lineHeight: 24,
  },
  form: {
    marginTop: Theme.spacing.lg,
  },
  inputGroup: {
    marginBottom: Theme.spacing.lg,
  },
  label: {
    fontSize: 14,
    color: Theme.colors.textDim,
    marginBottom: Theme.spacing.xs,
    marginLeft: 4,
  },
  input: {
    backgroundColor: Theme.colors.surface,
    borderRadius: Theme.borderRadius.md,
    padding: Theme.spacing.md,
    color: Theme.colors.text,
    fontSize: 16,
    borderWidth: 1,
    borderColor: 'rgba(255, 255, 255, 0.05)',
  },
  button: {
    backgroundColor: Theme.colors.primary,
    borderRadius: Theme.borderRadius.md,
    padding: Theme.spacing.md,
    alignItems: 'center',
    marginTop: Theme.spacing.md,
    shadowColor: Theme.colors.primary,
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 8,
    elevation: 5,
  },
  buttonText: {
    color: 'white',
    fontSize: 18,
    fontWeight: 'bold',
  },
});
