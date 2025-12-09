export interface Student {
  no: string;
  name: string;
  classNo: string;
  phoneNumber: string;
  gender: string;
}

export interface Book {
  issn: string;
  title: string;
  publisher: string;
  author: string;
  price: number;
  available: boolean;
  borrowerNo: string | null;
}

export interface History {
  id: number;
  studentNo: string;
  issn: string;
  borrowDate: string;
  returnDate: string | null;
  penalty: number;
}

export interface BorrowRequest {
  studentNo: string;
  issn: string;
  borrowDate: string;
}

export interface ReturnRequest {
  studentNo: string;
  issn: string;
  returnDate: string;
}

export interface LoanConfig {
  penaltyRate: number;
  maxBorrowDays: number;
}
