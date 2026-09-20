import { Search, Film } from 'lucide-react';
import { RatingCard } from './RatingCard';
import { Movie, EnabledRatings } from '../types/movie';

interface SearchResultsScreenProps {
  searchQuery: string;
  results: Movie[];
  onBack: () => void;
  onSelectMovie: (movie: Movie) => void;
  onSearchChange: (value: string) => void;
  enabledRatings?: EnabledRatings;
}

export function SearchResultsScreen({ searchQuery, results, onBack, onSelectMovie, onSearchChange, enabledRatings }: SearchResultsScreenProps) {
  return (
    <div className="min-h-screen bg-background">
      {/* PC Header */}
      <header className="bg-background/95 backdrop-blur-sm border-b border-border px-6 py-3 sticky top-0 z-50">
        <div className="flex items-center gap-6 max-w-7xl mx-auto">
          {/* Logo / Back */}
          <button
            onClick={onBack}
            className="flex items-center gap-2.5 flex-shrink-0 hover:opacity-70 transition-opacity"
            aria-label="Back to home"
          >
            <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
              <Film className="w-4 h-4 text-primary-foreground" />
            </div>
            <span className="text-base font-semibold tracking-tight">MovieRate</span>
          </button>

          {/* Search bar */}
          <div className="flex-1 max-w-2xl mx-auto">
            <div className="bg-secondary/50 rounded-xl border border-border px-4 py-2.5 flex items-center gap-3 hover:border-primary/40 focus-within:border-primary/60 transition-colors">
              <Search className="w-4 h-4 text-muted-foreground flex-shrink-0" />
              <input
                type="text"
                placeholder="Search for movies, directors..."
                value={searchQuery}
                onChange={(e) => onSearchChange(e.target.value)}
                autoFocus
                className="bg-transparent text-foreground placeholder-muted-foreground flex-1 outline-none text-sm min-w-0"
              />
            </div>
          </div>

          {/* Spacer to match header layout */}
          <div className="w-20 flex-shrink-0" />
        </div>
      </header>

      {/* Results */}
      <div className="px-6 py-6">
        <div className="max-w-7xl mx-auto">
          {/* Result count */}
          <div className="mb-5">
            {results.length > 0 ? (
              <p className="text-muted-foreground text-sm">
                <span className="text-foreground font-medium">{results.length}</span> result{results.length !== 1 ? 's' : ''} for &ldquo;{searchQuery}&rdquo;
              </p>
            ) : (
              <p className="text-muted-foreground text-sm">
                No results for &ldquo;{searchQuery}&rdquo;
              </p>
            )}
          </div>

          {results.length === 0 && (
            <div className="text-center py-20">
              <Search className="w-12 h-12 text-muted-foreground/30 mx-auto mb-4" />
              <p className="text-muted-foreground">Try a different title or director name</p>
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
            {results.map((movie) => (
              <RatingCard
                key={movie.id}
                title={movie.title}
                director={movie.director}
                year={movie.year}
                ratings={movie.ratings}
                posterUrl={movie.posterUrl}
                onClick={() => onSelectMovie(movie)}
                enabledRatings={enabledRatings}
              />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
