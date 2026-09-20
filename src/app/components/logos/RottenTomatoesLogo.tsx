export function RottenTomatoesLogo({ className }: { className?: string }) {
  return (
    <svg className={className} viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
      {/* Tomato */}
      <circle cx="16" cy="18" r="10" fill="#FA320A"/>
      {/* Highlight */}
      <ellipse cx="13" cy="15" rx="3" ry="4" fill="#FF6347" opacity="0.6"/>
      {/* Stem */}
      <path d="M16 8C16 8 14 6 12 7C10 8 11 10 11 10L16 8Z" fill="#00A550"/>
      {/* Leaf */}
      <path d="M16 8C16 8 18 6 20 7C22 8 21 10 21 10L16 8Z" fill="#00A550"/>
    </svg>
  );
}
