// Type declarations for chart.js and react-chartjs-2
// These packages should include their own types, but this is a fallback

declare module 'chart.js' {
  export const Chart: any;
  export const CategoryScale: any;
  export const LinearScale: any;
  export const PointElement: any;
  export const LineElement: any;
  export const Title: any;
  export const Tooltip: any;
  export const Legend: any;
}

declare module 'react-chartjs-2' {
  import { Component } from 'react';
  export class Line extends Component<any> {}
}

