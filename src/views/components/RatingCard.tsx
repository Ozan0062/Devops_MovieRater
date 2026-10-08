import { ImageWithFallback } from './ImageWithFallback';
import { ImdbLogo } from './logos/ImdbLogo';
import { RottenTomatoesLogo } from './logos/RottenTomatoesLogo';
import { MetacriticLogo } from './logos/MetacriticLogo';
import { Rating, EnabledRatings } from '../../models/movie';

interface RatingCardProps {
  title: string;
  director: string;
  ratings: Rating;
  onClick: () => void;
  posterUrl: string;
  year: number;
  enabledRatings?: EnabledRatings;
}

function getRatingColor(value: number, platform: 'imdb' | 'rt' | 'metacritic'): string {
  if (platform === 'imdb') {
    if (value >= 7) return 'text-green-400';
    if (value >= 5) return 'text-yellow-400';
    return 'text-red-400';
  } else if (platform === 'rt') {
    if (value >= 60) return 'text-green-400';
    if (value >= 40) return 'text-yellow-400';
    return 'text-red-400';
  } else {
    if (value >= 61) return 'text-green-400';
    if (value >= 40) return 'text-yellow-400';
    return 'text-red-400';
  }
}

export function RatingCard({ title, director, ratings, onClick, posterUrl, year, enabledRatings }: RatingCardProps) {
  // Only IMDb uses decimal format (1-10 with 1 decimal)
  const imdbRating = ratings.imdb.toFixed(1);
  
  const imdbColor = getRatingColor(ratings.imdb, 'imdb');
  const rtColor = getRatingColor(ratings.rottenTomatoes, 'rt');
  const metacriticColor = getRatingColor(ratings.metacritic, 'metacritic');

  // Default to all enabled if not provided
  const ratingsToShow = enabledRatings || { imdb: true, rottenTomatoes: true, metacritic: true };

  return (
    <button
      onClick={onClick}
      className="bg-card rounded-xl border border-border overflow-hidden text-left w-full group hover:border-primary/50 transition-all duration-300 hover:shadow-xl hover:shadow-primary/10 hover:-translate-y-1"
    >
      <div className="flex gap-4 p-4">
        {/* Poster */}
        <div className="relative w-28 h-40 flex-shrink-0 rounded-lg overflow-hidden">
          <ImageWithFallback
            src={posterUrl}
            alt={title}
            className="w-full h-full object-cover"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent" />
        </div>
        
        {/* Info section */}
        <div className="flex-1 min-w-0 flex flex-col py-1">
          {/* Title and Year */}
          <h3 className="text-foreground mb-1 line-clamp-2 group-hover:text-primary transition-colors leading-snug">
            {title}
          </h3>
          <p className="text-muted-foreground text-sm mb-2">{year}</p>

          {/* Director */}
          <p className="text-muted-foreground text-sm mb-3 truncate">
            Directed by {director}
          </p>
          
          {/* All three ratings side by side */}
          <div className="mt-auto space-y-2">
            <div className="flex items-center gap-3 flex-wrap">
              {/* IMDb */}
              {ratingsToShow.imdb && (
                <div className="flex items-center gap-2">
                  <ImdbLogo className="h-8 w-8 flex-shrink-0" />
                  <div className={imdbColor}>{imdbRating}</div>
                </div>
              )}

              {/* Rotten Tomatoes */}
              {ratingsToShow.rottenTomatoes && (
                <div className="flex items-center gap-2">
                  <RottenTomatoesLogo className="h-8 w-8 flex-shrink-0" />
                  <div className={rtColor}>{ratings.rottenTomatoes}%</div>
                </div>
              )}

              {/* Metacritic */}
              {ratingsToShow.metacritic && (
                <div className="flex items-center gap-2">
                  <MetacriticLogo className="h-7 w-7 flex-shrink-0" />
                  <div className={metacriticColor}>{ratings.metacritic}</div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </button>
  );
}
