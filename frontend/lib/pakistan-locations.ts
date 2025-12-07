// Pakistan States, Cities, and Towns Data
// Source: Pakistan Administrative Divisions

export interface Location {
  name: string;
  cities: {
    name: string;
    towns: string[];
  }[];
}

export const pakistanLocations: Record<string, Location> = {
  'Sindh': {
    name: 'Sindh',
    cities: [
      {
        name: 'Karachi',
        towns: [
          'Malir Cantt',
          'Gulshan-e-Iqbal',
          'Clifton',
          'Defence',
          'PECHS',
          'Gulistan-e-Jauhar',
          'North Nazimabad',
          'Liaquatabad',
          'Korangi',
          'Landhi',
          'Saddar',
          'Jamshed Town',
          'Shah Faisal',
          'Bin Qasim',
          'Gadap',
          'Keamari',
          'Lyari',
          'New Karachi',
          'Orangi',
          'SITE',
          'Surjani',
        ],
      },
      {
        name: 'Hyderabad',
        towns: [
          'Latifabad',
          'Qasimabad',
          'Hirabad',
          'City',
          'Hussainabad',
          'Pacca Qila',
          'Saddar',
        ],
      },
      {
        name: 'Sukkur',
        towns: [
          'City',
          'Rohri',
          'New Sukkur',
          'Old Sukkur',
        ],
      },
      {
        name: 'Larkana',
        towns: [
          'City',
          'Ratodero',
          'Shahdadkot',
        ],
      },
      {
        name: 'Nawabshah',
        towns: [
          'City',
          'Sakrand',
        ],
      },
      {
        name: 'Mirpur Khas',
        towns: [
          'City',
          'Digri',
          'Kot Ghulam Muhammad',
        ],
      },
    ],
  },
  'Punjab': {
    name: 'Punjab',
    cities: [
      {
        name: 'Lahore',
        towns: [
          'Gulberg',
          'Model Town',
          'Johar Town',
          'DHA',
          'Cantt',
          'Faisal Town',
          'Wapda Town',
          'Garden Town',
          'Shadman',
          'Saddar',
          'Anarkali',
          'Iqbal Town',
          'Samnabad',
          'Ravi Town',
          'Wagha',
        ],
      },
      {
        name: 'Faisalabad',
        towns: [
          'City',
          'Jaranwala',
          'Samundri',
          'Tandlianwala',
        ],
      },
      {
        name: 'Rawalpindi',
        towns: [
          'Cantt',
          'City',
          'Chaklala',
          'Gulraiz',
          'Bahria Town',
          'DHA',
          'Saddar',
        ],
      },
      {
        name: 'Multan',
        towns: [
          'City',
          'Cantt',
          'Shah Rukn-e-Alam',
          'Bosan',
        ],
      },
      {
        name: 'Gujranwala',
        towns: [
          'City',
          'Cantt',
          'Kamoke',
        ],
      },
      {
        name: 'Sialkot',
        towns: [
          'City',
          'Cantt',
          'Daska',
        ],
      },
      {
        name: 'Islamabad',
        towns: [
          'F-6',
          'F-7',
          'F-8',
          'G-6',
          'G-7',
          'G-8',
          'I-8',
          'I-9',
          'I-10',
          'DHA',
          'Bahria Town',
          'Cantt',
        ],
      },
      {
        name: 'Sargodha',
        towns: [
          'City',
          'Cantt',
        ],
      },
      {
        name: 'Bahawalpur',
        towns: [
          'City',
          'Cantt',
        ],
      },
      {
        name: 'Sheikhupura',
        towns: [
          'City',
          'Muridke',
        ],
      },
    ],
  },
  'Khyber Pakhtunkhwa': {
    name: 'Khyber Pakhtunkhwa',
    cities: [
      {
        name: 'Peshawar',
        towns: [
          'Cantt',
          'City',
          'University Town',
          'Hayatabad',
          'Charsadda Road',
        ],
      },
      {
        name: 'Mardan',
        towns: [
          'City',
          'Cantt',
        ],
      },
      {
        name: 'Abbottabad',
        towns: [
          'Cantt',
          'City',
          'Havelian',
        ],
      },
      {
        name: 'Swat',
        towns: [
          'Mingora',
          'Saidu Sharif',
        ],
      },
      {
        name: 'Kohat',
        towns: [
          'City',
          'Cantt',
        ],
      },
    ],
  },
  'Balochistan': {
    name: 'Balochistan',
    cities: [
      {
        name: 'Quetta',
        towns: [
          'Cantt',
          'City',
          'Sariab',
          'Kuchlak',
        ],
      },
      {
        name: 'Turbat',
        towns: [
          'City',
        ],
      },
      {
        name: 'Gwadar',
        towns: [
          'City',
          'Port',
        ],
      },
    ],
  },
  'Gilgit-Baltistan': {
    name: 'Gilgit-Baltistan',
    cities: [
      {
        name: 'Gilgit',
        towns: [
          'City',
          'Cantt',
        ],
      },
      {
        name: 'Skardu',
        towns: [
          'City',
        ],
      },
    ],
  },
  'Azad Jammu and Kashmir': {
    name: 'Azad Jammu and Kashmir',
    cities: [
      {
        name: 'Muzaffarabad',
        towns: [
          'City',
        ],
      },
      {
        name: 'Mirpur',
        towns: [
          'City',
        ],
      },
    ],
  },
};

// Helper functions
export const getStates = (): string[] => {
  return Object.keys(pakistanLocations);
};

export const getCities = (state: string): string[] => {
  if (!state || !pakistanLocations[state]) return [];
  return pakistanLocations[state].cities.map(city => city.name);
};

export const getTowns = (state: string, city: string): string[] => {
  if (!state || !city || !pakistanLocations[state]) return [];
  const cityData = pakistanLocations[state].cities.find(c => c.name === city);
  return cityData ? cityData.towns : [];
};

