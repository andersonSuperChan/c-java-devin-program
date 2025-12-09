import { Student, Book, History, BorrowRequest, ReturnRequest, LoanConfig } from '../types';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const error = await response.json().catch(() => ({ error: 'Unknown error' }));
    throw new Error(error.error || `HTTP error! status: ${response.status}`);
  }
  return response.json();
}

export const studentApi = {
  getAll: async (): Promise<Student[]> => {
    const response = await fetch(`${API_URL}/api/students`);
    return handleResponse<Student[]>(response);
  },

  getByNo: async (no: string): Promise<Student> => {
    const response = await fetch(`${API_URL}/api/students/${no}`);
    return handleResponse<Student>(response);
  },

  create: async (student: Student): Promise<Student> => {
    const response = await fetch(`${API_URL}/api/students`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(student),
    });
    return handleResponse<Student>(response);
  },

  update: async (no: string, student: Student): Promise<Student> => {
    const response = await fetch(`${API_URL}/api/students/${no}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(student),
    });
    return handleResponse<Student>(response);
  },

  delete: async (no: string): Promise<void> => {
    const response = await fetch(`${API_URL}/api/students/${no}`, {
      method: 'DELETE',
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
  },
};

export const bookApi = {
  getAll: async (): Promise<Book[]> => {
    const response = await fetch(`${API_URL}/api/books`);
    return handleResponse<Book[]>(response);
  },

  getByIssn: async (issn: string): Promise<Book> => {
    const response = await fetch(`${API_URL}/api/books/${issn}`);
    return handleResponse<Book>(response);
  },

  getAvailable: async (): Promise<Book[]> => {
    const response = await fetch(`${API_URL}/api/books/available`);
    return handleResponse<Book[]>(response);
  },

  create: async (book: Book): Promise<Book> => {
    const response = await fetch(`${API_URL}/api/books`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(book),
    });
    return handleResponse<Book>(response);
  },

  update: async (issn: string, book: Book): Promise<Book> => {
    const response = await fetch(`${API_URL}/api/books/${issn}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(book),
    });
    return handleResponse<Book>(response);
  },

  delete: async (issn: string): Promise<void> => {
    const response = await fetch(`${API_URL}/api/books/${issn}`, {
      method: 'DELETE',
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
  },
};

export const loanApi = {
  borrow: async (request: BorrowRequest): Promise<History> => {
    const response = await fetch(`${API_URL}/api/loans/borrow`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    });
    return handleResponse<History>(response);
  },

  return: async (request: ReturnRequest): Promise<History> => {
    const response = await fetch(`${API_URL}/api/loans/return`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    });
    return handleResponse<History>(response);
  },

  getOverdue: async (asOfDate?: string): Promise<History[]> => {
    const url = asOfDate 
      ? `${API_URL}/api/loans/overdue?asOfDate=${asOfDate}`
      : `${API_URL}/api/loans/overdue`;
    const response = await fetch(url);
    return handleResponse<History[]>(response);
  },

  getConfig: async (): Promise<LoanConfig> => {
    const response = await fetch(`${API_URL}/api/loans/config`);
    return handleResponse<LoanConfig>(response);
  },
};

export const historyApi = {
  getAll: async (): Promise<History[]> => {
    const response = await fetch(`${API_URL}/api/history`);
    return handleResponse<History[]>(response);
  },

  getByStudentNo: async (studentNo: string): Promise<History[]> => {
    const response = await fetch(`${API_URL}/api/history/student/${studentNo}`);
    return handleResponse<History[]>(response);
  },

  getByIssn: async (issn: string): Promise<History[]> => {
    const response = await fetch(`${API_URL}/api/history/book/${issn}`);
    return handleResponse<History[]>(response);
  },
};
