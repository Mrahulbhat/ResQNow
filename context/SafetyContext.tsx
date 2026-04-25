import React, { createContext, useContext, useState, useEffect } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';

export type Settings = {
  emergencyContactName: string;
  emergencyContactPhone: string;
  policeStationPhone: string;
  startTime: string; // HH:mm
  endTime: string; // HH:mm
  isAlwaysOn: boolean;
  isMonitoring: boolean;
};

type SafetyContextType = {
  settings: Settings;
  updateSettings: (newSettings: Partial<Settings>) => Promise<void>;
  isLoading: boolean;
};

const DEFAULT_SETTINGS: Settings = {
  emergencyContactName: '',
  emergencyContactPhone: '',
  policeStationPhone: '',
  startTime: '09:00',
  endTime: '20:00',
  isAlwaysOn: false,
  isMonitoring: false,
};

const SafetyContext = createContext<SafetyContextType | undefined>(undefined);

export const SafetyProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [settings, setSettings] = useState<Settings>(DEFAULT_SETTINGS);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    loadSettings();
  }, []);

  const loadSettings = async () => {
    try {
      const saved = await AsyncStorage.getItem('resqnow_settings');
      if (saved) {
        setSettings(JSON.parse(saved));
      }
    } catch (e) {
      console.error('Failed to load settings', e);
    } finally {
      setIsLoading(false);
    }
  };

  const updateSettings = async (newSettings: Partial<Settings>) => {
    try {
      const updated = { ...settings, ...newSettings };
      setSettings(updated);
      await AsyncStorage.setItem('resqnow_settings', JSON.stringify(updated));
    } catch (e) {
      console.error('Failed to save settings', e);
    }
  };

  return (
    <SafetyContext.Provider value={{ settings, updateSettings, isLoading }}>
      {children}
    </SafetyContext.Provider>
  );
};

export const useSafety = () => {
  const context = useContext(SafetyContext);
  if (!context) {
    throw new Error('useSafety must be used within a SafetyProvider');
  }
  return context;
};
