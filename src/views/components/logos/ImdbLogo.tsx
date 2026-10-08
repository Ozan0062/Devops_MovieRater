import imdbLogo from '../../../assets/imdb-logo.png';

export function ImdbLogo({ className }: { className?: string }) {
  return (
    <img 
      src={imdbLogo} 
      alt="IMDb" 
      className={`object-contain ${className ?? ''}`}
    />
  );
}
