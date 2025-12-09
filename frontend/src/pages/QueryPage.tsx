import { useState, useEffect } from 'react';
import { Student, Book, History, LoanConfig } from '../types';
import { studentApi, bookApi, historyApi, loanApi } from '../services/api';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Badge } from '@/components/ui/badge';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Search, BookOpen, User, Clock, AlertTriangle } from 'lucide-react';

export default function QueryPage() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [config, setConfig] = useState<LoanConfig | null>(null);

  const [bookSearch, setBookSearch] = useState('');
  const [bookResult, setBookResult] = useState<Book | null>(null);

  const [studentSearch, setStudentSearch] = useState('');
  const [studentResult, setStudentResult] = useState<Student | null>(null);

  const [historySearch, setHistorySearch] = useState('');
  const [historyResults, setHistoryResults] = useState<History[]>([]);

  const [overdueDate, setOverdueDate] = useState(new Date().toISOString().split('T')[0]);
  const [overdueResults, setOverdueResults] = useState<History[]>([]);
  const [overdueStudents, setOverdueStudents] = useState<Map<string, Student>>(new Map());

  useEffect(() => {
    loadConfig();
  }, []);

  const loadConfig = async () => {
    try {
      const configData = await loanApi.getConfig();
      setConfig(configData);
    } catch (err) {
      console.error('Failed to load config:', err);
    }
  };

  const searchBook = async () => {
    try {
      setLoading(true);
      setError(null);
      setBookResult(null);
      const book = await bookApi.getByIssn(bookSearch);
      setBookResult(book);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Book not found');
    } finally {
      setLoading(false);
    }
  };

  const searchStudent = async () => {
    try {
      setLoading(true);
      setError(null);
      setStudentResult(null);
      const student = await studentApi.getByNo(studentSearch);
      setStudentResult(student);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Student not found');
    } finally {
      setLoading(false);
    }
  };

  const searchHistory = async () => {
    try {
      setLoading(true);
      setError(null);
      setHistoryResults([]);
      const history = await historyApi.getByStudentNo(historySearch);
      setHistoryResults(history);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to search history');
    } finally {
      setLoading(false);
    }
  };

  const searchOverdue = async () => {
    try {
      setLoading(true);
      setError(null);
      setOverdueResults([]);
      setOverdueStudents(new Map());
      const overdue = await loanApi.getOverdue(overdueDate);
      setOverdueResults(overdue);

      const studentMap = new Map<string, Student>();
      for (const h of overdue) {
        if (!studentMap.has(h.studentNo)) {
          try {
            const student = await studentApi.getByNo(h.studentNo);
            studentMap.set(h.studentNo, student);
          } catch {
            // Student not found, skip
          }
        }
      }
      setOverdueStudents(studentMap);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to search overdue books');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Query System</h1>

      {config && (
        <Card>
          <CardContent className="pt-4">
            <div className="flex gap-4 text-sm text-gray-600">
              <span>Penalty Rate: {config.penaltyRate} yuan/day</span>
              <span>Max Borrow Days: {config.maxBorrowDays} days</span>
            </div>
          </CardContent>
        </Card>
      )}

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
          {error}
        </div>
      )}

      <Tabs defaultValue="book" className="w-full">
        <TabsList className="grid w-full grid-cols-4">
          <TabsTrigger value="book">
            <BookOpen className="w-4 h-4 mr-2" />
            Book
          </TabsTrigger>
          <TabsTrigger value="student">
            <User className="w-4 h-4 mr-2" />
            Student
          </TabsTrigger>
          <TabsTrigger value="history">
            <Clock className="w-4 h-4 mr-2" />
            History
          </TabsTrigger>
          <TabsTrigger value="overdue">
            <AlertTriangle className="w-4 h-4 mr-2" />
            Overdue
          </TabsTrigger>
        </TabsList>

        <TabsContent value="book">
          <Card>
            <CardHeader>
              <CardTitle>Search Book by ISSN</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex gap-2">
                <Input
                  placeholder="Enter ISSN"
                  value={bookSearch}
                  onChange={(e) => setBookSearch(e.target.value)}
                />
                <Button onClick={searchBook} disabled={loading}>
                  <Search className="w-4 h-4 mr-2" />
                  Search
                </Button>
              </div>
              {bookResult && (
                <div className="border rounded p-4 space-y-2">
                  <h3 className="font-bold text-lg">{bookResult.title}</h3>
                  <div className="grid grid-cols-2 gap-2 text-sm">
                    <div><Label>ISSN:</Label> {bookResult.issn}</div>
                    <div><Label>Author:</Label> {bookResult.author}</div>
                    <div><Label>Publisher:</Label> {bookResult.publisher}</div>
                    <div><Label>Price:</Label> {bookResult.price.toFixed(2)} yuan</div>
                    <div>
                      <Label>Status:</Label>{' '}
                      <Badge variant={bookResult.available ? 'default' : 'secondary'}>
                        {bookResult.available ? 'Available' : 'Borrowed'}
                      </Badge>
                    </div>
                    <div><Label>Borrower:</Label> {bookResult.borrowerNo || 'None'}</div>
                  </div>
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="student">
          <Card>
            <CardHeader>
              <CardTitle>Search Student by ID</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex gap-2">
                <Input
                  placeholder="Enter Student ID"
                  value={studentSearch}
                  onChange={(e) => setStudentSearch(e.target.value)}
                />
                <Button onClick={searchStudent} disabled={loading}>
                  <Search className="w-4 h-4 mr-2" />
                  Search
                </Button>
              </div>
              {studentResult && (
                <div className="border rounded p-4 space-y-2">
                  <h3 className="font-bold text-lg">{studentResult.name}</h3>
                  <div className="grid grid-cols-2 gap-2 text-sm">
                    <div><Label>Student ID:</Label> {studentResult.no}</div>
                    <div><Label>Class:</Label> {studentResult.classNo}</div>
                    <div><Label>Phone:</Label> {studentResult.phoneNumber}</div>
                    <div><Label>Gender:</Label> {studentResult.gender === 'M' ? 'Male' : 'Female'}</div>
                  </div>
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="history">
          <Card>
            <CardHeader>
              <CardTitle>Search Borrow History by Student ID</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex gap-2">
                <Input
                  placeholder="Enter Student ID"
                  value={historySearch}
                  onChange={(e) => setHistorySearch(e.target.value)}
                />
                <Button onClick={searchHistory} disabled={loading}>
                  <Search className="w-4 h-4 mr-2" />
                  Search
                </Button>
              </div>
              {historyResults.length > 0 && (
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Book ISSN</TableHead>
                      <TableHead>Borrow Date</TableHead>
                      <TableHead>Return Date</TableHead>
                      <TableHead>Penalty</TableHead>
                      <TableHead>Status</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {historyResults.map((h) => (
                      <TableRow key={h.id}>
                        <TableCell>{h.issn}</TableCell>
                        <TableCell>{h.borrowDate}</TableCell>
                        <TableCell>{h.returnDate || '-'}</TableCell>
                        <TableCell>{h.penalty.toFixed(2)} yuan</TableCell>
                        <TableCell>
                          <Badge variant={h.returnDate ? 'default' : 'secondary'}>
                            {h.returnDate ? 'Returned' : 'Not Returned'}
                          </Badge>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              )}
              {historyResults.length === 0 && historySearch && !loading && (
                <p className="text-gray-500 text-center">No history found for this student</p>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="overdue">
          <Card>
            <CardHeader>
              <CardTitle>Overdue Books Reminder</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex gap-2">
                <Input
                  type="date"
                  value={overdueDate}
                  onChange={(e) => setOverdueDate(e.target.value)}
                />
                <Button onClick={searchOverdue} disabled={loading}>
                  <AlertTriangle className="w-4 h-4 mr-2" />
                  Check Overdue
                </Button>
              </div>
              {overdueResults.length > 0 && (
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Book ISSN</TableHead>
                      <TableHead>Student ID</TableHead>
                      <TableHead>Student Name</TableHead>
                      <TableHead>Phone</TableHead>
                      <TableHead>Borrow Date</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {overdueResults.map((h) => {
                      const student = overdueStudents.get(h.studentNo);
                      return (
                        <TableRow key={h.id}>
                          <TableCell>{h.issn}</TableCell>
                          <TableCell>{h.studentNo}</TableCell>
                          <TableCell>{student?.name || 'Unknown'}</TableCell>
                          <TableCell>{student?.phoneNumber || 'Unknown'}</TableCell>
                          <TableCell>{h.borrowDate}</TableCell>
                        </TableRow>
                      );
                    })}
                  </TableBody>
                </Table>
              )}
              {overdueResults.length === 0 && !loading && (
                <p className="text-gray-500 text-center">No overdue books found</p>
              )}
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
