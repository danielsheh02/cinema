import axios from 'axios';
import MovieService from '../services/movie.service';
import authHeader from '../services/auth_header.service';

jest.mock('axios');
jest.mock('../services/auth_header.service');

describe('MovieService', () => {
  const mockTokenHeader = { Authorization: 'Bearer test-token' };
  const API_BASE = 'http://localhost:30099';

  beforeEach(() => {
    authHeader.mockReturnValue(mockTokenHeader);
    jest.spyOn(console, 'error').mockImplementation(() => {});
    jest.spyOn(console, 'log').mockImplementation(() => {});
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  test('createMovie should send POST request and return data', async () => {
    const mockData = { id: 1, name: 'Test Movie', timeDuration: 'PT2H0M' };
    axios.post.mockResolvedValue({ data: mockData });

    const result = await MovieService.createMovie('Test Movie', 7200);

    expect(axios.post).toHaveBeenCalledWith(
      `${API_BASE}/api/movies/admin`,
      { name: 'Test Movie', timeDuration: 7200 },
      { headers: mockTokenHeader }
    );
    expect(result).toEqual(mockData);
  });

  test('createMovie should throw error on failure', async () => {
    const mockError = new Error('Network Error');
    axios.post.mockRejectedValue(mockError);

    await expect(MovieService.createMovie('Error Movie', 6000)).rejects.toThrow('Network Error');

    expect(axios.post).toHaveBeenCalledWith(
      `${API_BASE}/api/movies/admin`,
      { name: 'Error Movie', timeDuration: 6000 },
      { headers: mockTokenHeader }
    );
  });

  test('getMovies should send GET request and return data', async () => {
    const mockData = [
      { id: 1, name: 'Movie 1', timeDuration: 'PT1H30M' },
      { id: 2, name: 'Movie 2', timeDuration: 'PT2H0M' },
    ];
    axios.get.mockResolvedValue({ data: mockData });

    const result = await MovieService.getMovies();

    expect(axios.get).toHaveBeenCalledWith(
      `${API_BASE}/api/movies`,
      { headers: mockTokenHeader }
    );
    expect(result).toEqual(mockData);
  });

  test('getMovies should throw error on failure', async () => {
    const mockError = new Error('Failed to fetch');
    axios.get.mockRejectedValue(mockError);

    await expect(MovieService.getMovies()).rejects.toThrow('Failed to fetch');

    expect(axios.get).toHaveBeenCalledWith(
      `${API_BASE}/api/movies`,
      { headers: mockTokenHeader }
    );
  });

  test('deleteMovie should send DELETE request', async () => {
    axios.delete.mockResolvedValue({ status: 204 });

    await MovieService.deleteMovie(42);

    expect(axios.delete).toHaveBeenCalledWith(
      `${API_BASE}/api/movies/admin/42`,
      { headers: mockTokenHeader }
    );
  });

  test('updateMovie should send PUT request and return data', async () => {
    const mockResponse = { id: 42, name: 'Updated Movie', timeDuration: 'PT2H30M' };
    axios.put.mockResolvedValue({ data: mockResponse });

    const result = await MovieService.updateMovie(42, 'Updated Movie', 9000);

    expect(axios.put).toHaveBeenCalledWith(
      `${API_BASE}/api/movies/admin/42`,
      { name: 'Updated Movie', timeDuration: 9000 },
      { headers: mockTokenHeader }
    );
    expect(result).toEqual(mockResponse);
  });

  test('updateMovie should throw error on failure', async () => {
    const mockError = new Error('Update failed');
    axios.put.mockRejectedValue(mockError);

    await expect(MovieService.updateMovie(42, 'Fail Movie', 8000)).rejects.toThrow('Update failed');

    expect(axios.put).toHaveBeenCalledWith(
      `${API_BASE}/api/movies/admin/42`,
      { name: 'Fail Movie', timeDuration: 8000 },
      { headers: mockTokenHeader }
    );
  });
});
