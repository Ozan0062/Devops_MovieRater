import metacriticLogo from '../../../assets/metacritic-logo.png';

export function MetacriticLogo({ className }: { className?: string }) {
  return (
    <img 
      src={metacriticLogo} 
      alt="Metacritic" 
      className={`object-contain ${className ?? ''}`}
    />
  );
}
