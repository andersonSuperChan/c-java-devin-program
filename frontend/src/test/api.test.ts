import { describe, it, expect, vi, beforeEach } from "vitest";
import { studentApi, bookApi, loanApi, historyApi } from "../services/api";

const mockFetch = vi.fn();
global.fetch = mockFetch;

describe("studentApi", () => {
  beforeEach(() => {
    mockFetch.mockClear();
  });

  it("getAll fetches all students", async () => {
    const students = [
      { no: "S001", name: "John Doe", classNo: "Class1", phoneNumber: "123456789", gender: "M" },
    ];
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(students),
    });

    const result = await studentApi.getAll();

    expect(mockFetch).toHaveBeenCalledWith(expect.stringContaining("/api/students"));
    expect(result).toEqual(students);
  });

  it("getByNo fetches a student by ID", async () => {
    const student = { no: "S001", name: "John Doe", classNo: "Class1", phoneNumber: "123456789", gender: "M" };
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(student),
    });

    const result = await studentApi.getByNo("S001");

    expect(mockFetch).toHaveBeenCalledWith(expect.stringContaining("/api/students/S001"));
    expect(result).toEqual(student);
  });

  it("create posts a new student", async () => {
    const student = { no: "S001", name: "John Doe", classNo: "Class1", phoneNumber: "123456789", gender: "M" };
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(student),
    });

    const result = await studentApi.create(student);

    expect(mockFetch).toHaveBeenCalledWith(
      expect.stringContaining("/api/students"),
      expect.objectContaining({
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(student),
      })
    );
    expect(result).toEqual(student);
  });

  it("update puts an updated student", async () => {
    const student = { no: "S001", name: "John Updated", classNo: "Class2", phoneNumber: "111111111", gender: "M" };
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(student),
    });

    const result = await studentApi.update("S001", student);

    expect(mockFetch).toHaveBeenCalledWith(
      expect.stringContaining("/api/students/S001"),
      expect.objectContaining({
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(student),
      })
    );
    expect(result).toEqual(student);
  });

  it("delete removes a student", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: true,
    });

    await studentApi.delete("S001");

    expect(mockFetch).toHaveBeenCalledWith(
      expect.stringContaining("/api/students/S001"),
      expect.objectContaining({
        method: "DELETE",
      })
    );
  });

  it("throws error on failed request", async () => {
    mockFetch.mockResolvedValueOnce({
      ok: false,
      status: 404,
      statusText: "Not Found",
    });

    await expect(studentApi.getByNo("S999")).rejects.toThrow();
  });
});

describe("bookApi", () => {
  beforeEach(() => {
    mockFetch.mockClear();
  });

  it("getAll fetches all books", async () => {
    const books = [
      { issn: "B001", title: "Test Book", publisher: "Publisher", author: "Author", price: 29.99, available: true, borrowerNo: null },
    ];
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(books),
    });

    const result = await bookApi.getAll();

    expect(mockFetch).toHaveBeenCalledWith(expect.stringContaining("/api/books"));
    expect(result).toEqual(books);
  });

  it("getByIssn fetches a book by ISSN", async () => {
    const book = { issn: "B001", title: "Test Book", publisher: "Publisher", author: "Author", price: 29.99, available: true, borrowerNo: null };
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(book),
    });

    const result = await bookApi.getByIssn("B001");

    expect(mockFetch).toHaveBeenCalledWith(expect.stringContaining("/api/books/B001"));
    expect(result).toEqual(book);
  });

  it("getAvailable fetches only available books", async () => {
    const books = [
      { issn: "B001", title: "Test Book", publisher: "Publisher", author: "Author", price: 29.99, available: true, borrowerNo: null },
    ];
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(books),
    });

    const result = await bookApi.getAvailable();

    expect(mockFetch).toHaveBeenCalledWith(expect.stringContaining("/api/books/available"));
    expect(result).toEqual(books);
  });

  it("create posts a new book", async () => {
    const book = { issn: "B001", title: "Test Book", publisher: "Publisher", author: "Author", price: 29.99 };
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve({ ...book, available: true, borrowerNo: null }),
    });

    const result = await bookApi.create(book);

    expect(mockFetch).toHaveBeenCalledWith(
      expect.stringContaining("/api/books"),
      expect.objectContaining({
        method: "POST",
        headers: { "Content-Type": "application/json" },
      })
    );
    expect(result.available).toBe(true);
  });
});

describe("loanApi", () => {
  beforeEach(() => {
    mockFetch.mockClear();
  });

  it("borrow posts a borrow request", async () => {
    const history = { id: 1, studentNo: "S001", issn: "B001", borrowDate: "2025-01-01", returnDate: null, penalty: 0 };
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(history),
    });

    const request = { studentNo: "S001", issn: "B001", borrowDate: "2025-01-01" };
    const result = await loanApi.borrow(request);

    expect(mockFetch).toHaveBeenCalledWith(
      expect.stringContaining("/api/loans/borrow"),
      expect.objectContaining({
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(request),
      })
    );
    expect(result).toEqual(history);
  });

  it("return posts a return request", async () => {
    const history = { id: 1, studentNo: "S001", issn: "B001", borrowDate: "2025-01-01", returnDate: "2025-02-10", penalty: 0 };
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(history),
    });

    const request = { studentNo: "S001", issn: "B001", returnDate: "2025-02-10" };
    const result = await loanApi.return(request);

    expect(mockFetch).toHaveBeenCalledWith(
      expect.stringContaining("/api/loans/return"),
      expect.objectContaining({
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(request),
      })
    );
    expect(result).toEqual(history);
  });

  it("getOverdue fetches overdue loans", async () => {
    const overdueLoans = [{ id: 1, studentNo: "S001", issn: "B001", borrowDate: "2025-01-01", returnDate: null, penalty: 0 }];
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(overdueLoans),
    });

    const result = await loanApi.getOverdue("2025-03-15");

    expect(mockFetch).toHaveBeenCalledWith(expect.stringContaining("/api/loans/overdue?asOfDate=2025-03-15"));
    expect(result).toEqual(overdueLoans);
  });

  it("getConfig fetches loan configuration", async () => {
    const config = { penaltyRate: 0.1, maxBorrowDays: 60 };
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(config),
    });

    const result = await loanApi.getConfig();

    expect(mockFetch).toHaveBeenCalledWith(expect.stringContaining("/api/loans/config"));
    expect(result).toEqual(config);
  });
});

describe("historyApi", () => {
  beforeEach(() => {
    mockFetch.mockClear();
  });

  it("getAll fetches all history", async () => {
    const history = [{ id: 1, studentNo: "S001", issn: "B001", borrowDate: "2025-01-01", returnDate: null, penalty: 0 }];
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(history),
    });

    const result = await historyApi.getAll();

    expect(mockFetch).toHaveBeenCalledWith(expect.stringContaining("/api/history"));
    expect(result).toEqual(history);
  });

  it("getByStudentNo fetches history by student ID", async () => {
    const history = [{ id: 1, studentNo: "S001", issn: "B001", borrowDate: "2025-01-01", returnDate: null, penalty: 0 }];
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(history),
    });

    const result = await historyApi.getByStudentNo("S001");

    expect(mockFetch).toHaveBeenCalledWith(expect.stringContaining("/api/history/student/S001"));
    expect(result).toEqual(history);
  });

  it("getByIssn fetches history by book ISSN", async () => {
    const history = [{ id: 1, studentNo: "S001", issn: "B001", borrowDate: "2025-01-01", returnDate: null, penalty: 0 }];
    mockFetch.mockResolvedValueOnce({
      ok: true,
      json: () => Promise.resolve(history),
    });

    const result = await historyApi.getByIssn("B001");

    expect(mockFetch).toHaveBeenCalledWith(expect.stringContaining("/api/history/book/B001"));
    expect(result).toEqual(history);
  });
});
