---
name: Premium Aviation Ethos
colors:
  surface: '#f8f9ff'
  surface-dim: '#cbdbf5'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e5eeff'
  surface-container-high: '#dce9ff'
  surface-container-highest: '#d3e4fe'
  on-surface: '#0b1c30'
  on-surface-variant: '#45464f'
  inverse-surface: '#213145'
  inverse-on-surface: '#eaf1ff'
  outline: '#757680'
  outline-variant: '#c5c6d0'
  surface-tint: '#4d5c90'
  primary: '#051849'
  on-primary: '#ffffff'
  primary-container: '#1e2e5f'
  on-primary-container: '#8897ce'
  inverse-primary: '#b5c4ff'
  secondary: '#795900'
  on-secondary: '#ffffff'
  secondary-container: '#febf0d'
  on-secondary-container: '#6d5000'
  tertiary: '#2f1500'
  on-tertiary: '#ffffff'
  tertiary-container: '#4d2700'
  on-tertiary-container: '#c68c5c'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dce1ff'
  primary-fixed-dim: '#b5c4ff'
  on-primary-fixed: '#041749'
  on-primary-fixed-variant: '#354476'
  secondary-fixed: '#ffdfa0'
  secondary-fixed-dim: '#fbbc05'
  on-secondary-fixed: '#261a00'
  on-secondary-fixed-variant: '#5c4300'
  tertiary-fixed: '#ffdcc2'
  tertiary-fixed-dim: '#f9b985'
  on-tertiary-fixed: '#2e1500'
  on-tertiary-fixed-variant: '#683c13'
  background: '#f8f9ff'
  on-background: '#0b1c30'
  surface-variant: '#d3e4fe'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 48px
    fontWeight: '700'
    lineHeight: '1.1'
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '600'
    lineHeight: '1.3'
  title-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 18px
    fontWeight: '600'
    lineHeight: '1.4'
  body-base:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.6'
  body-sm:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: '1.5'
  label-caps:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: '1.2'
    letterSpacing: 0.05em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 8px
  xs: 4px
  sm: 12px
  md: 24px
  lg: 48px
  xl: 80px
  container-max: 1280px
  gutter: 24px
---

## Brand & Style
This design system embodies the sophistication of premium air travel, blending corporate reliability with the warmth of high-end hospitality. The brand personality is "The Discerning Navigator"—authoritative yet welcoming, ensuring a seamless journey from booking to boarding. 

The aesthetic follows a **Corporate / Modern** style with heavy influences from **Minimalism**. The interface relies on a card-based architecture to organize complex travel data into digestible units. To avoid a cold, utilitarian feel, the system uses organic rounded corners and soft, ambient depth to evoke a sense of comfort and ease.

## Colors
The palette is anchored by a deep "Midnight Sky" blue, representing stability and the vastness of flight. The vibrant "Solar Gold" accent is used sparingly for primary actions and critical information, providing high visibility without overwhelming the professional tone. 

- **Primary (#1e2e5f):** Used for headers, primary buttons, and structural branding elements.
- **Accent (#fbbc04):** Reserved for Call-to-Actions (CTAs), active selection states, and meaningful highlights.
- **Neutral (#64748b):** Utilized for secondary text, borders, and inactive icons to maintain a clean hierarchy.
- **Surface:** A bright, off-white background keeps the interface feeling airy and expansive.

## Typography
The system utilizes two distinct sans-serif families to balance character with legibility. **Plus Jakarta Sans** is used for headlines to provide a friendly, modern, and slightly rounded geometric feel. **Inter** is the workhorse for body copy and data-heavy tables, chosen for its exceptional readability at small sizes and professional, neutral tone. All labels for airport codes (e.g., JFK, LHR) should use the `label-caps` style for maximum clarity.

## Layout & Spacing
The design system employs a **Fixed Grid** model for desktop, centered within the viewport to maintain a premium, editorial feel. A 12-column grid is used with generous 24px gutters. 

Vertical rhythm is built on an 8px base unit. Card containers should utilize `md` (24px) padding to ensure content feels uncrowded. Large sections of the passenger journey (e.g., Seat Selection vs. Payment) should be separated by `xl` (80px) vertical spacing to reduce cognitive load.

## Elevation & Depth
Hierarchy is conveyed through **Tonal Layers** and **Ambient Shadows**. 
- **Level 0 (Background):** The base canvas in #f8fafc.
- **Level 1 (Cards):** Pure white (#ffffff) surfaces with a soft, diffused shadow (0px 4px 20px rgba(30, 46, 95, 0.08)). This "tinted shadow" uses a hint of the primary blue to maintain color harmony.
- **Level 2 (Interactive/Hover):** When a user interacts with a card or flight option, the shadow deepens and the element lifts slightly.
- **Header:** The fixed header uses a subtle bottom border rather than a shadow to keep the top of the UI feeling light and integrated.

## Shapes
A "Rounded" shape language is applied across the system to evoke the ergonomic curves of aircraft interiors. 
- **Standard UI elements** (Buttons, Inputs): 0.5rem (8px) radius.
- **Cards & Containers:** 1rem (16px) radius to emphasize the modular, card-based UI.
- **Search Bars:** Should utilize a pill-shaped (full rounded) radius to distinguish the "Search" function from content cards.

## Components
- **Fixed Header:** Always pinned to the top. The company logo and name are left-aligned. The right side features a notification bell with a vibrant yellow dot for active alerts.
- **Cards:** The primary container. Cards should have a white background, 16px corner radius, and the standard ambient shadow. Use internal dividers sparingly; prefer whitespace to separate flight times from prices.
- **Buttons:** Primary buttons are #1e2e5f with white text. Secondary buttons use a #1e2e5f ghost style (outline). The yellow accent (#fbbc04) is used for "Promoted" or "Upgrade" actions.
- **Icons:** Must be multi-colored and "friendly." Use 2-point line weights with rounded terminals. For flight status, use colored backgrounds (e.g., a soft green circle behind a checkmark).
- **Inputs:** Use floating labels to save space. Focus states should be indicated by a 2px solid primary blue border.
- **Flight Chips:** Small, 8px rounded badges used to indicate "Non-stop," "Business Class," or "Eco-friendly" options, using low-saturation background tints of the primary colors.