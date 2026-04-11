export type Paginated<T> = {
  content: T[];
  page: {
    size: number;
    number: number;
    totalElements: number;
    totalPages: number;
  };
};

export type PaginationParams = {
  page?: number;
  size?: number;
};
