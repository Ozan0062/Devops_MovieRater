import {
  ChevronDown,
  SlidersHorizontal,
  X,
  ArrowUpDown,
  Check,
} from "lucide-react";
import { useState } from "react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "./ui/dropdown-menu";

export interface FilterOptions {
  genres: string[];
  years: number[];
  minRating: number;
  sortBy: "imdb" | "rottenTomatoes" | "metacritic" | "year" | "title" | "views";
  sortOrder: "asc" | "desc";
}

interface FilterBarProps {
  filters: FilterOptions;
  onFilterChange: (filters: FilterOptions) => void;
  availableGenres: string[];
  availableYears: number[];
}

const SORT_OPTIONS: { label: string; sortBy: FilterOptions["sortBy"]; sortOrder: "asc" | "desc" }[] = [
  { label: "Most Popular", sortBy: "views", sortOrder: "desc" },
  { label: "IMDb: High to Low", sortBy: "imdb", sortOrder: "desc" },
  { label: "IMDb: Low to High", sortBy: "imdb", sortOrder: "asc" },
  { label: "Rotten Tomatoes: High to Low", sortBy: "rottenTomatoes", sortOrder: "desc" },
  { label: "Rotten Tomatoes: Low to High", sortBy: "rottenTomatoes", sortOrder: "asc" },
  { label: "Metacritic: High to Low", sortBy: "metacritic", sortOrder: "desc" },
  { label: "Metacritic: Low to High", sortBy: "metacritic", sortOrder: "asc" },
  { label: "Newest First", sortBy: "year", sortOrder: "desc" },
  { label: "Oldest First", sortBy: "year", sortOrder: "asc" },
  { label: "Title A–Z", sortBy: "title", sortOrder: "asc" },
  { label: "Title Z–A", sortBy: "title", sortOrder: "desc" },
];

export function FilterBar({ filters, onFilterChange, availableGenres, availableYears }: FilterBarProps) {
  const [isOpen, setIsOpen] = useState(false);

  const toggleGenre = (genre: string) => {
    const newGenres = filters.genres.includes(genre)
      ? filters.genres.filter((g) => g !== genre)
      : [...filters.genres, genre];
    onFilterChange({ ...filters, genres: newGenres });
  };

  const toggleYear = (year: number) => {
    const newYears = filters.years.includes(year)
      ? filters.years.filter((y) => y !== year)
      : [...filters.years, year];
    onFilterChange({ ...filters, years: newYears });
  };

  const clearFilters = () => {
    onFilterChange({ genres: [], years: [], minRating: 0, sortBy: "views", sortOrder: "desc" });
  };

  const hasActiveFilters = filters.genres.length > 0 || filters.years.length > 0 || filters.minRating > 0;

  const currentSortLabel =
    SORT_OPTIONS.find((o) => o.sortBy === filters.sortBy && o.sortOrder === filters.sortOrder)?.label ?? "Sort";

  const activeFilterCount = filters.genres.length + filters.years.length + (filters.minRating > 0 ? 1 : 0);

  return (
    <div className="mb-5 sticky top-[57px] z-30 bg-background pt-2 pb-3">
      {/* Toolbar row */}
      <div className="flex items-center gap-2">
        {/* Filter toggle */}
        <button
          onClick={() => setIsOpen(!isOpen)}
          className={`flex items-center gap-2 px-4 py-2 rounded-lg border text-sm transition-colors ${
            isOpen || hasActiveFilters
              ? "bg-primary text-primary-foreground border-primary"
              : "bg-secondary border-border hover:bg-secondary/80"
          }`}
        >
          <SlidersHorizontal className="w-4 h-4" />
          <span>Filters</span>
          {activeFilterCount > 0 && (
            <span className="bg-primary-foreground/20 text-primary-foreground rounded-full w-5 h-5 flex items-center justify-center text-xs font-medium">
              {activeFilterCount}
            </span>
          )}
          <ChevronDown className={`w-4 h-4 transition-transform ${isOpen ? "rotate-180" : ""}`} />
        </button>

        {/* Sort dropdown */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <button className="flex items-center gap-2 px-4 py-2 bg-secondary rounded-lg border border-border hover:bg-secondary/80 transition-colors text-sm">
              <ArrowUpDown className="w-4 h-4" />
              <span>{currentSortLabel}</span>
              <ChevronDown className="w-3.5 h-3.5 text-muted-foreground" />
            </button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="start" className="w-56">
            {SORT_OPTIONS.map((option, i) => {
              const isActive = filters.sortBy === option.sortBy && filters.sortOrder === option.sortOrder;
              const prevOption = SORT_OPTIONS[i - 1];
              const showSeparator = i > 0 && prevOption.sortBy !== option.sortBy;
              return (
                <div key={`${option.sortBy}-${option.sortOrder}`}>
                  {showSeparator && <DropdownMenuSeparator />}
                  <DropdownMenuItem
                    onClick={() => onFilterChange({ ...filters, sortBy: option.sortBy, sortOrder: option.sortOrder })}
                    className={`flex items-center justify-between cursor-pointer ${isActive ? "font-medium" : ""}`}
                  >
                    <span>{option.label}</span>
                    {isActive && <Check className="w-4 h-4 text-primary" />}
                  </DropdownMenuItem>
                </div>
              );
            })}
          </DropdownMenuContent>
        </DropdownMenu>

        {/* Clear button */}
        {hasActiveFilters && (
          <button
            onClick={clearFilters}
            className="ml-auto flex items-center gap-1.5 px-3 py-2 text-sm text-destructive hover:bg-destructive/10 rounded-lg transition-colors"
          >
            <X className="w-4 h-4" />
            <span>Clear filters</span>
          </button>
        )}
      </div>

      {/* Active filter chips */}
      {hasActiveFilters && (
        <div className="flex items-center gap-2 mt-2 flex-wrap">
          {filters.genres.map((g) => (
            <span
              key={g}
              className="flex items-center gap-1.5 px-2.5 py-1 bg-primary/10 text-primary text-xs rounded-full"
            >
              {g}
              <button onClick={() => toggleGenre(g)} className="hover:opacity-70">
                <X className="w-3 h-3" />
              </button>
            </span>
          ))}
          {filters.years.map((y) => (
            <span
              key={y}
              className="flex items-center gap-1.5 px-2.5 py-1 bg-primary/10 text-primary text-xs rounded-full"
            >
              {y}
              <button onClick={() => toggleYear(y)} className="hover:opacity-70">
                <X className="w-3 h-3" />
              </button>
            </span>
          ))}
          {filters.minRating > 0 && (
            <span className="flex items-center gap-1.5 px-2.5 py-1 bg-primary/10 text-primary text-xs rounded-full">
              IMDb ≥ {filters.minRating.toFixed(1)}
              <button
                onClick={() => onFilterChange({ ...filters, minRating: 0 })}
                className="hover:opacity-70"
              >
                <X className="w-3 h-3" />
              </button>
            </span>
          )}
        </div>
      )}

      {/* Expanded filter panel */}
      {isOpen && (
        <div className="mt-3 p-5 bg-card rounded-xl border border-border">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {/* Genre */}
            <div>
              <h4 className="text-xs font-medium text-muted-foreground uppercase tracking-wider mb-3">Genre</h4>
              <div className="flex flex-wrap gap-2">
                {availableGenres.map((genre) => (
                  <button
                    key={genre}
                    onClick={() => toggleGenre(genre)}
                    className={`px-3 py-1.5 rounded-lg text-sm transition-colors ${
                      filters.genres.includes(genre)
                        ? "bg-primary text-primary-foreground"
                        : "bg-secondary hover:bg-secondary/80"
                    }`}
                  >
                    {genre}
                  </button>
                ))}
              </div>
            </div>

            {/* Year */}
            <div>
              <h4 className="text-xs font-medium text-muted-foreground uppercase tracking-wider mb-3">Year</h4>
              <div className="flex flex-wrap gap-2">
                {availableYears.map((year) => (
                  <button
                    key={year}
                    onClick={() => toggleYear(year)}
                    className={`px-3 py-1.5 rounded-lg text-sm transition-colors ${
                      filters.years.includes(year)
                        ? "bg-primary text-primary-foreground"
                        : "bg-secondary hover:bg-secondary/80"
                    }`}
                  >
                    {year}
                  </button>
                ))}
              </div>
            </div>

            {/* Minimum IMDb rating */}
            <div>
              <h4 className="text-xs font-medium text-muted-foreground uppercase tracking-wider mb-3">
                Min. IMDb Rating
                {filters.minRating > 0 && (
                  <span className="ml-2 text-primary normal-case font-normal">
                    {filters.minRating.toFixed(1)} / 10
                  </span>
                )}
              </h4>
              <input
                type="range"
                min="0"
                max="10"
                step="0.5"
                value={filters.minRating}
                onChange={(e) => onFilterChange({ ...filters, minRating: parseFloat(e.target.value) })}
                className="w-full h-2 bg-secondary rounded-lg appearance-none cursor-pointer pc-slider"
              />
              <div className="flex justify-between text-xs text-muted-foreground mt-1">
                <span>Any</span>
                <span>10</span>
              </div>
              <style>{`
                .pc-slider::-webkit-slider-thumb {
                  appearance: none;
                  width: 16px;
                  height: 16px;
                  background: #6366f1;
                  cursor: pointer;
                  border-radius: 50%;
                  border: 2px solid white;
                  box-shadow: 0 1px 3px rgba(0,0,0,0.2);
                }
                .pc-slider::-moz-range-thumb {
                  width: 16px;
                  height: 16px;
                  background: #6366f1;
                  cursor: pointer;
                  border-radius: 50%;
                  border: 2px solid white;
                  box-shadow: 0 1px 3px rgba(0,0,0,0.2);
                }
              `}</style>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
