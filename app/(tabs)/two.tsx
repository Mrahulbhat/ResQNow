import React, { useState } from 'react';
import { StyleSheet, View, Text, ScrollView, TextInput, TouchableOpacity, Switch, Alert } from 'react-native';
import { useSafety } from '../../context/SafetyContext';
import { Theme } from '../../constants/Theme';
import { Ionicons } from '@expo/vector-icons';

export default function SettingsScreen() {
  const { settings, updateSettings } = useSafety();
  const [localSettings, setLocalSettings] = useState(settings);

  const handleSave = async () => {
    await updateSettings(localSettings);
    Alert.alert('Success', 'Settings updated successfully.');
  };

  return (
    <ScrollView style={styles.container} contentContainerStyle={styles.content}>
      <View style={styles.header}>
        <Text style={styles.title}>Configuration</Text>
        <Text style={styles.subtitle}>Customize your safety preferences and time limits.</Text>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Active Hours</Text>
        <View style={styles.card}>
          <View style={styles.row}>
            <Text style={styles.rowLabel}>Always On (24/7)</Text>
            <Switch 
              value={localSettings.isAlwaysOn} 
              onValueChange={(val) => setLocalSettings({...localSettings, isAlwaysOn: val})}
              trackColor={{ false: '#3e3e3e', true: Theme.colors.success }}
            />
          </View>
          
          {!localSettings.isAlwaysOn && (
            <View style={styles.timeContainer}>
              <View style={styles.timeInput}>
                <Text style={styles.timeLabel}>Start Time</Text>
                <TextInput 
                  style={styles.input}
                  value={localSettings.startTime}
                  onChangeText={(text) => setLocalSettings({...localSettings, startTime: text})}
                  placeholder="09:00"
                  placeholderTextColor={Theme.colors.textDim}
                />
              </View>
              <View style={styles.timeInput}>
                <Text style={styles.timeLabel}>End Time</Text>
                <TextInput 
                  style={styles.input}
                  value={localSettings.endTime}
                  onChangeText={(text) => setLocalSettings({...localSettings, endTime: text})}
                  placeholder="20:00"
                  placeholderTextColor={Theme.colors.textDim}
                />
              </View>
            </View>
          )}
          <Text style={styles.helperText}>Setting specific hours helps optimize battery usage.</Text>
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Emergency Contacts</Text>
        <View style={styles.card}>
          <View style={styles.inputGroup}>
            <Text style={styles.inputLabel}>Parent/Guardian Phone</Text>
            <TextInput 
              style={styles.input}
              value={localSettings.emergencyContactPhone}
              onChangeText={(text) => setLocalSettings({...localSettings, emergencyContactPhone: text})}
              keyboardType="phone-pad"
              placeholder="+1234567890"
              placeholderTextColor={Theme.colors.textDim}
            />
          </View>
          <View style={styles.inputGroup}>
            <Text style={styles.inputLabel}>Police Station Number</Text>
            <TextInput 
              style={styles.input}
              value={localSettings.policeStationPhone}
              onChangeText={(text) => setLocalSettings({...localSettings, policeStationPhone: text})}
              keyboardType="phone-pad"
              placeholder="911"
              placeholderTextColor={Theme.colors.textDim}
            />
          </View>
        </View>
      </View>

      <TouchableOpacity style={styles.saveButton} onPress={handleSave}>
        <Text style={styles.saveButtonText}>Save Changes</Text>
      </TouchableOpacity>
      
      <View style={styles.infoBox}>
        <Ionicons name="information-circle-outline" size={20} color={Theme.colors.textDim} />
        <Text style={styles.infoText}>
          ResQNow recognizes "Help", "Bachavo", and "Kaapadi" in 22 languages automatically when active.
        </Text>
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Theme.colors.background,
  },
  content: {
    padding: Theme.spacing.lg,
    paddingTop: 60,
    paddingBottom: 40,
  },
  header: {
    marginBottom: Theme.spacing.xl,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    color: Theme.colors.text,
  },
  subtitle: {
    fontSize: 16,
    color: Theme.colors.textDim,
    marginTop: 4,
  },
  section: {
    marginBottom: Theme.spacing.lg,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: Theme.colors.text,
    marginBottom: Theme.spacing.sm,
    marginLeft: 4,
  },
  card: {
    backgroundColor: Theme.colors.surface,
    borderRadius: Theme.borderRadius.lg,
    padding: Theme.spacing.md,
    borderWidth: 1,
    borderColor: 'rgba(255, 255, 255, 0.05)',
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: Theme.spacing.sm,
  },
  rowLabel: {
    fontSize: 16,
    color: Theme.colors.text,
  },
  timeContainer: {
    flexDirection: 'row',
    gap: Theme.spacing.md,
    marginTop: Theme.spacing.md,
  },
  timeInput: {
    flex: 1,
  },
  timeLabel: {
    fontSize: 12,
    color: Theme.colors.textDim,
    marginBottom: 4,
  },
  inputGroup: {
    marginBottom: Theme.spacing.md,
  },
  inputLabel: {
    fontSize: 12,
    color: Theme.colors.textDim,
    marginBottom: 4,
  },
  input: {
    backgroundColor: 'rgba(0,0,0,0.2)',
    borderRadius: Theme.borderRadius.md,
    padding: Theme.spacing.sm,
    color: Theme.colors.text,
    fontSize: 16,
    borderWidth: 1,
    borderColor: 'rgba(255, 255, 255, 0.1)',
  },
  helperText: {
    fontSize: 12,
    color: Theme.colors.textDim,
    marginTop: Theme.spacing.md,
    fontStyle: 'italic',
  },
  saveButton: {
    backgroundColor: Theme.colors.secondary,
    padding: Theme.spacing.md,
    borderRadius: Theme.borderRadius.md,
    alignItems: 'center',
    marginTop: Theme.spacing.lg,
  },
  saveButtonText: {
    color: 'white',
    fontSize: 18,
    fontWeight: 'bold',
  },
  infoBox: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: Theme.spacing.xl,
    backgroundColor: 'rgba(255, 255, 255, 0.05)',
    padding: Theme.spacing.md,
    borderRadius: Theme.borderRadius.md,
    gap: Theme.spacing.sm,
  },
  infoText: {
    flex: 1,
    fontSize: 13,
    color: Theme.colors.textDim,
    lineHeight: 18,
  },
});
