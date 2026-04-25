import React, { useEffect, useState } from 'react';
import { StyleSheet, TouchableOpacity, View, Text, Animated, Alert } from 'react-native';
import { useSafety } from '../../context/SafetyContext';
import { Theme } from '../../constants/Theme';
import { Ionicons } from '@expo/vector-icons';
import { registerSafetyTask, stopSafetyTask, triggerEmergency } from '../../services/SafetyService';
import * as Location from 'expo-location';

export default function SafetyDashboard() {
  const { settings, updateSettings } = useSafety();
  const [pulseAnim] = useState(new Animated.Value(1));

  useEffect(() => {
    if (settings.isMonitoring) {
      startPulse();
      registerSafetyTask();
    } else {
      stopPulse();
      stopSafetyTask();
    }
  }, [settings.isMonitoring]);

  const startPulse = () => {
    Animated.loop(
      Animated.sequence([
        Animated.timing(pulseAnim, {
          toValue: 1.1,
          duration: 1000,
          useNativeDriver: true,
        }),
        Animated.timing(pulseAnim, {
          toValue: 1,
          duration: 1000,
          useNativeDriver: true,
        }),
      ])
    ).start();
  };

  const stopPulse = () => {
    pulseAnim.setValue(1);
    Animated.timing(pulseAnim).stop();
  };

  const toggleMonitoring = async () => {
    const { status } = await Location.requestForegroundPermissionsAsync();
    if (status !== 'granted') {
      Alert.alert('Permission Denied', 'Location access is required for ResQNow to function.');
      return;
    }

    const backgroundStatus = await Location.requestBackgroundPermissionsAsync();
    if (backgroundStatus.status !== 'granted') {
      Alert.alert('Permission Denied', 'Background location access is required for continuous protection.');
      return;
    }

    updateSettings({ isMonitoring: !settings.isMonitoring });
  };

  const handleManualTrigger = () => {
    Alert.alert(
      'Emergency Trigger',
      'Are you sure you want to trigger an emergency alert? This will call the police and your emergency contacts.',
      [
        { text: 'Cancel', style: 'cancel' },
        { text: 'YES, TRIGGER', style: 'destructive', onPress: () => triggerEmergency() },
      ]
    );
  };

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.greeting}>Hello, Security is Active</Text>
        <Text style={styles.statusText}>
          {settings.isMonitoring ? 'ResQNow is protecting you' : 'Protection is currently paused'}
        </Text>
      </View>

      <View style={styles.mainContent}>
        <Animated.View style={[
          styles.pulseContainer,
          { transform: [{ scale: pulseAnim }] }
        ]}>
          <TouchableOpacity 
            style={[
              styles.shieldButton,
              settings.isMonitoring ? styles.shieldActive : styles.shieldInactive
            ]} 
            onPress={toggleMonitoring}
          >
            <Ionicons 
              name={settings.isMonitoring ? "shield-checkmark" : "shield-outline"} 
              size={100} 
              color="white" 
            />
          </TouchableOpacity>
        </Animated.View>

        <Text style={styles.instructionText}>
          {settings.isMonitoring ? 'Tap to Deactivate' : 'Tap to Activate Protection'}
        </Text>
      </View>

      <View style={styles.footer}>
        <TouchableOpacity 
          style={[styles.emergencyButton, { backgroundColor: Theme.colors.secondary, marginBottom: Theme.spacing.md }]} 
          onPress={() => Alert.alert('Voice Detection Active', 'Listening for "Help", "Bachavo", "Kaapadi"... (Simulated)')}
        >
          <Text style={styles.emergencyButtonText}>TEST VOICE TRIGGER</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.emergencyButton} onPress={handleManualTrigger}>
          <Text style={styles.emergencyButtonText}>PANIC BUTTON</Text>
        </TouchableOpacity>
        
        <View style={styles.statsContainer}>
          <View style={styles.statBox}>
            <Ionicons name="time-outline" size={24} color={Theme.colors.primary} />
            <Text style={styles.statLabel}>Active Hours</Text>
            <Text style={styles.statValue}>{settings.isAlwaysOn ? '24/7' : `${settings.startTime} - ${settings.endTime}`}</Text>
          </View>
          <View style={styles.statBox}>
            <Ionicons name="people-outline" size={24} color={Theme.colors.secondary} />
            <Text style={styles.statLabel}>Contact</Text>
            <Text style={styles.statValue}>{settings.emergencyContactName || 'Not Set'}</Text>
          </View>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Theme.colors.background,
    padding: Theme.spacing.lg,
  },
  header: {
    marginTop: 60,
    marginBottom: 40,
  },
  greeting: {
    fontSize: 24,
    fontWeight: 'bold',
    color: Theme.colors.text,
  },
  statusText: {
    fontSize: 16,
    color: Theme.colors.textDim,
    marginTop: 4,
  },
  mainContent: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  pulseContainer: {
    width: 240,
    height: 240,
    justifyContent: 'center',
    alignItems: 'center',
  },
  shieldButton: {
    width: 200,
    height: 200,
    borderRadius: 100,
    justifyContent: 'center',
    alignItems: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 10 },
    shadowOpacity: 0.5,
    shadowRadius: 15,
    elevation: 10,
  },
  shieldActive: {
    backgroundColor: Theme.colors.success,
  },
  shieldInactive: {
    backgroundColor: Theme.colors.surface,
    borderWidth: 2,
    borderColor: Theme.colors.textDim,
  },
  instructionText: {
    marginTop: 30,
    fontSize: 18,
    color: Theme.colors.text,
    fontWeight: '600',
  },
  footer: {
    marginBottom: 40,
  },
  emergencyButton: {
    backgroundColor: Theme.colors.primary,
    padding: Theme.spacing.md,
    borderRadius: Theme.borderRadius.md,
    alignItems: 'center',
    marginBottom: Theme.spacing.lg,
  },
  emergencyButtonText: {
    color: 'white',
    fontSize: 20,
    fontWeight: '900',
    letterSpacing: 1,
  },
  statsContainer: {
    flexDirection: 'row',
    gap: Theme.spacing.md,
  },
  statBox: {
    flex: 1,
    backgroundColor: Theme.colors.surface,
    padding: Theme.spacing.md,
    borderRadius: Theme.borderRadius.md,
    alignItems: 'center',
  },
  statLabel: {
    color: Theme.colors.textDim,
    fontSize: 12,
    marginTop: 4,
  },
  statValue: {
    color: Theme.colors.text,
    fontSize: 14,
    fontWeight: 'bold',
    marginTop: 2,
  },
});
