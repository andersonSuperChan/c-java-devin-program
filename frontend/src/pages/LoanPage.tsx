import { useState, useEffect } from 'react';
import { Student, Book, History } from '../types';
import { studentApi, bookApi, loanApi } from '../services/api';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { BookOpen, BookCheck } from 'lucide-react';

export default function LoanPage() {
  const [students, setStudents] = useState<Student[]>([]);
  const [, setBooks] = useState<Book[]>([]);
  const [availableBooks, setAvailableBooks] = useState<Book[]>([]);
  const [borrowedBooks, setBorrowedBooks] = useState<Book[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [result, setResult] = useState<History | null>(null);

  const [borrowForm, setBorrowForm] = useState({
    studentNo: '',
    issn: '',
    borrowDate: new Date().toISOString().split('T')[0],
  });

  const [returnForm, setReturnForm] = useState({
    studentNo: '',
    issn: '',
    returnDate: new Date().toISOString().split('T')[0],
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [studentsData, booksData] = await Promise.all([
        studentApi.getAll(),
        bookApi.getAll(),
      ]);
      setStudents(studentsData);
      setBooks(booksData);
      setAvailableBooks(booksData.filter((b) => b.available));
      setBorrowedBooks(booksData.filter((b) => !b.available));
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const handleBorrow = async () => {
    try {
      setError(null);
      setSuccess(null);
      setResult(null);
      const history = await loanApi.borrow(borrowForm);
      setResult(history);
      setSuccess('Book borrowed successfully!');
      setBorrowForm({
        studentNo: '',
        issn: '',
        borrowDate: new Date().toISOString().split('T')[0],
      });
      loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to borrow book');
    }
  };

  const handleReturn = async () => {
    try {
      setError(null);
      setSuccess(null);
      setResult(null);
      const history = await loanApi.return(returnForm);
      setResult(history);
      if (history.penalty > 0) {
        setSuccess(`Book returned successfully! Penalty: ${history.penalty.toFixed(2)} yuan`);
      } else {
        setSuccess('Book returned successfully! No penalty.');
      }
      setReturnForm({
        studentNo: '',
        issn: '',
        returnDate: new Date().toISOString().split('T')[0],
      });
      loadData();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to return book');
    }
  };

  if (loading) {
    return <div className="flex justify-center items-center h-64">Loading...</div>;
  }

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Borrow / Return Books</h1>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      {success && (
        <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded">
          {success}
        </div>
      )}

      <Tabs defaultValue="borrow" className="w-full">
        <TabsList className="grid w-full grid-cols-2">
          <TabsTrigger value="borrow">
            <BookOpen className="w-4 h-4 mr-2" />
            Borrow Book
          </TabsTrigger>
          <TabsTrigger value="return">
            <BookCheck className="w-4 h-4 mr-2" />
            Return Book
          </TabsTrigger>
        </TabsList>

        <TabsContent value="borrow">
          <Card>
            <CardHeader>
              <CardTitle>Borrow a Book</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <Label htmlFor="borrow-student">Student</Label>
                <Select
                  value={borrowForm.studentNo}
                  onValueChange={(value) => setBorrowForm({ ...borrowForm, studentNo: value })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select a student" />
                  </SelectTrigger>
                  <SelectContent>
                    {students.map((student) => (
                      <SelectItem key={student.no} value={student.no}>
                        {student.no} - {student.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div>
                <Label htmlFor="borrow-book">Book (Available)</Label>
                <Select
                  value={borrowForm.issn}
                  onValueChange={(value) => setBorrowForm({ ...borrowForm, issn: value })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select a book" />
                  </SelectTrigger>
                  <SelectContent>
                    {availableBooks.map((book) => (
                      <SelectItem key={book.issn} value={book.issn}>
                        {book.issn} - {book.title}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div>
                <Label htmlFor="borrow-date">Borrow Date</Label>
                <Input
                  id="borrow-date"
                  type="date"
                  value={borrowForm.borrowDate}
                  onChange={(e) => setBorrowForm({ ...borrowForm, borrowDate: e.target.value })}
                />
              </div>
              <Button onClick={handleBorrow} className="w-full">
                <BookOpen className="w-4 h-4 mr-2" />
                Borrow Book
              </Button>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="return">
          <Card>
            <CardHeader>
              <CardTitle>Return a Book</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <Label htmlFor="return-student">Student</Label>
                <Select
                  value={returnForm.studentNo}
                  onValueChange={(value) => setReturnForm({ ...returnForm, studentNo: value })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select a student" />
                  </SelectTrigger>
                  <SelectContent>
                    {students.map((student) => (
                      <SelectItem key={student.no} value={student.no}>
                        {student.no} - {student.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div>
                <Label htmlFor="return-book">Book (Borrowed)</Label>
                <Select
                  value={returnForm.issn}
                  onValueChange={(value) => setReturnForm({ ...returnForm, issn: value })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select a book" />
                  </SelectTrigger>
                  <SelectContent>
                    {borrowedBooks.map((book) => (
                      <SelectItem key={book.issn} value={book.issn}>
                        {book.issn} - {book.title} (Borrowed by: {book.borrowerNo})
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div>
                <Label htmlFor="return-date">Return Date</Label>
                <Input
                  id="return-date"
                  type="date"
                  value={returnForm.returnDate}
                  onChange={(e) => setReturnForm({ ...returnForm, returnDate: e.target.value })}
                />
              </div>
              <Button onClick={handleReturn} className="w-full">
                <BookCheck className="w-4 h-4 mr-2" />
                Return Book
              </Button>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      {result && (
        <Card>
          <CardHeader>
            <CardTitle>Transaction Result</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label>Student ID</Label>
                <p className="font-medium">{result.studentNo}</p>
              </div>
              <div>
                <Label>Book ISSN</Label>
                <p className="font-medium">{result.issn}</p>
              </div>
              <div>
                <Label>Borrow Date</Label>
                <p className="font-medium">{result.borrowDate}</p>
              </div>
              <div>
                <Label>Return Date</Label>
                <p className="font-medium">{result.returnDate || 'Not returned'}</p>
              </div>
              <div>
                <Label>Penalty</Label>
                <p className="font-medium">{result.penalty.toFixed(2)} yuan</p>
              </div>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
