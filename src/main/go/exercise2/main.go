package main

import (
	"fmt"
	"math/rand"
)

type MatchResult struct {
	playerA int
	playerB int
	winner  int
}

func Player(pid int, m int, players []chan int, matchResults []chan MatchResult, done chan int) {

	fmt.Printf("Hi from Player %d\n", pid)

	playerLen := 1 << m

	opponentId := pid
	if pid%2 == 0 {
		opponentId = pid + 1
	} else {
		opponentId = pid - 1
	}

	matchNum := playerLen / 2

	for round := range m {
		choose := rand.Intn(2)
		number := rand.Intn(6)
		opponentChoose := 1 - choose
		opponentNumber := 0
		playerA := 0
		playerB := 0

		if pid < opponentId {
			playerA = pid
			playerB = opponentId
			fmt.Printf("[Player %d] -> Starting match against player %d\n", pid, opponentId)
			players[opponentId] <- choose
			opponentChoose = <-players[pid]
		} else {
			playerA = opponentId
			playerB = pid
			opponentChoose = <-players[pid]
			choose = 1 - opponentChoose
			players[opponentId] <- choose
		}

		fmt.Printf("[Player %d] -> Player %d choose %d\n", pid, opponentId, opponentChoose)

		if pid < opponentId {
			players[opponentId] <- number
			opponentNumber = <-players[pid]
		} else {
			opponentNumber = <-players[pid]
			players[opponentId] <- number
		}

		isOdd := (number+opponentNumber)%2 == 0

		if (choose == 0 && isOdd == true) ||
			(choose == 1 && isOdd == false) {

			fmt.Printf("[Player %d] -> I won!\n", pid)

			result := MatchResult{
				playerA: playerA,
				playerB: playerB,
				winner:  pid,
			}

			for i := range playerLen {
				if i != pid {
					matchResults[i] <- result
				}
			}

			if round == m-1 {
				done <- pid
				return
			}

			roundResults := make([]MatchResult, 0, matchNum-1)

			for i := 0; i < matchNum-1; i++ {
				matchResult := <-matchResults[pid]
				roundResults = append(roundResults, matchResult)
			}

			groupSize := 1 << (round + 2)
			halfGroupSize := groupSize / 2
			otherGroupStart := (pid / groupSize) * groupSize
			if pid < otherGroupStart+halfGroupSize {
				otherGroupStart = otherGroupStart + halfGroupSize
			}

			for _, result := range roundResults {
				if result.playerA >= otherGroupStart && result.playerA < otherGroupStart+halfGroupSize {
					opponentId = result.winner
					fmt.Printf("[Player %d] -> Next opponent: Player %d\n", pid, opponentId)
					break
				}
			}

			matchNum = matchNum / 2

		} else {
			fmt.Printf("[Player %d] -> I lost!\n", pid)
			break
		}
	}
}

func main() {
	m := 3
	playerLen := 1 << m

	players := make([]chan int, playerLen)
	matchResults := make([]chan MatchResult, playerLen)

	done := make(chan int)

	for i := range playerLen {
		players[i] = make(chan int)
		matchResults[i] = make(chan MatchResult, playerLen*m)
	}

	for i := range playerLen {
		go Player(i, m, players, matchResults, done)
	}

	winner := <-done

	fmt.Printf("\nTOURNAMENT WINNER: Player %d\n", winner)
}
